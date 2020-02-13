package com.randonautica.app.Classes;

public class SendEntropy {

    final String entropy;
    final String size;
    final String timestamp;

    SendEntropy(final String entropy,
                final String size,
                final String timestamp) {

        this.entropy = entropy;
        this.size = size;
        this.timestamp = timestamp;
    }

    public class Response {
        private int EntropySize;

        private long Timestamp;

        private String Gid;

        public int getEntropySize() {
            return EntropySize;
        }

        public long getTimestamp() {
            return Timestamp;
        }

        public String getGid() {
            return Gid;
        }
    }

}
