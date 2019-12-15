package com.example.Randonaut;



public class Attractors {

    private atts attractors;

    public atts getAttractors() {
        return attractors;
    }

    public class atts {
        private int type;

        private double radiusM;

        private double power;

        private double z_score;

        public int getType() {
            return type;
        }

        public double getRadiusM() {
            return radiusM;
        }

        public double getPower() {
            return power;
        }

        public double getZ_score() {
            return z_score;
        }

        private Center center;

        public Center getCenter() {
            return center;
        }

        public class Center {

            private Latlng Latlng;

            public Latlng getLatlng() {
                return Latlng;
            }

            public class Latlng{

                private Point point;

                public Point getPoint() {
                    return point;
                }

                public class Point {

                    private double latitude;
                    private double longitude;

                    public double getLatitude() {
                        return latitude;
                    }

                    public double getLongitude() {
                        return longitude;
                    }
                }

            }

        }
    }
}



