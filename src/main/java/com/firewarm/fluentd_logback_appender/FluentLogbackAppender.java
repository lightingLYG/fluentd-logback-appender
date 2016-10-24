/**
 * Copyright (c) 2012 sndyuk <sanada@sndyuk.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.firewarm.fluentd_logback_appender;

import java.util.HashMap;
import java.util.Map;

import org.fluentd.logger.FluentLogger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class FluentLogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

//    private static final int MSG_SIZE_LIMIT = 65535;

    private static final class FluentDaemonAppender extends DaemonAppender<ILoggingEvent> {

        private FluentLogger fluentLogger;
        private final String tag;
        private final String label;
        private final String remoteHost;
        private final int port;
        private final Layout<ILoggingEvent> layout;
        private final Map<String, String> additionalFields;

        FluentDaemonAppender(String tag, String label, String remoteHost, int port, Layout<ILoggingEvent> layout, int maxQueueSize,Map<String, String> additionalFields) {
            super(maxQueueSize);
            this.tag = tag;
            this.label = label;
            this.remoteHost = remoteHost;
            this.port = port;
            this.layout = layout;
            this.additionalFields=additionalFields;
        }

        @Override
        public void execute() {
            this.fluentLogger = FluentLogger.getLogger(label != null ? tag : null, remoteHost, port);
            super.execute();
        }

        @Override
        protected void close() {
            try {
                super.close();
            } finally {
                if (fluentLogger != null) {
                    fluentLogger.close();
                }
            }
        }

        @Override
        protected void append(ILoggingEvent rawData) {
            String msg = null;
            if (layout != null) {
                msg = layout.doLayout(rawData);
            } else {
                msg = rawData.toString();
            }
//            if (msg != null && msg.length() > MSG_SIZE_LIMIT) {
//                msg = msg.substring(0, MSG_SIZE_LIMIT);
//            }
            Map<String, Object> data = new HashMap<String, Object>(1);
            if (additionalFields != null) {
                data.putAll(additionalFields);
            }
            data.put("msg", msg);
            if (label == null) {
                fluentLogger.log(tag, data);
            } else {
                fluentLogger.log(label, data);
            }
        }
    }

    private DaemonAppender<ILoggingEvent> appender;

    // Max queue size of logs which is waiting to be sent (When it reach to the max size, the log will be disappeared).
    private int maxQueueSize;
    private Map<String,String> additionalFields;

	@Override
    protected void append(ILoggingEvent eventObject) {
        if (appender == null) {
            synchronized (this) {
                appender = new FluentDaemonAppender(tag, label, remoteHost, port, layout, maxQueueSize,additionalFields);
            }
        }
        eventObject.prepareForDeferredProcessing();
        appender.log(eventObject);
    }

    @Override
    public void stop() {
        try {
            super.stop();
        } finally {
            if (appender != null) {
                appender.close();
            }
        }
    }

    private String tag;
    private String label;
    private String remoteHost;
    private int port;
    private Layout<ILoggingEvent> layout;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Layout<ILoggingEvent> getLayout() {
        return layout;
    }

    public void setLayout(Layout<ILoggingEvent> layout) {
        this.layout = layout;
    }
    
    /*public Map<String, String> getAdditionalFields() {
		return additionalFields;
	}

	public void setAdditionalFields(Map<String, String> additionalFields) {
		this.additionalFields = additionalFields;
	}*/
	public void addAdditionalField(Field field) {
        if (additionalFields == null) {
            additionalFields = new HashMap<String, String>();
        }
        additionalFields.put(field.getKey(), field.getValue());
    }
	public static class Field {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}