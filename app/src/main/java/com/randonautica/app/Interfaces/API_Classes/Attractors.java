package com.randonautica.app.Interfaces.API_Classes;

//Used by RandoWrapperApi

public class Attractors {

    private atts attractors;

    public atts getAttractors() {
        return attractors;
    }

    public class atts {

        private String GID;

        private String TID;

        private String LID;

        private int type;

        private double x;

        private double y;

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public int getType() {
            return type;
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
                private Bearing bearing;

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

                public Bearing getBearing() {
                    return bearing;
                }

                public class Bearing {

                    private double distance;
                    private double initialBearing;
                    private double finalBearing;

                    public double getDistance() {
                        return distance;
                    }

                    public double getInitialBearing() {
                        return initialBearing;
                    }

                    public double getFinalBearing() {
                        return finalBearing;
                    }
                }

            }
        }

        private int side;

        private double distanceErr;

        private double radiusM;

        private int n;

        private double mean;

        private int rarity;

        private double power_old;

        private double power;

        private double z_score;

        private double probability_single;

        private double integral_score;

        private double significance;

        private double probability;

        private int FILTERING_SIGNIFICANCE;

        public String getGID() {
            return GID;
        }

        public String getTID() {
            return TID;
        }

        public String getLID() {
            return LID;
        }

        public int getSide() {
            return side;
        }

        public double getDistanceErr() {
            return distanceErr;
        }

        public double getRadiusM() {
            return radiusM;
        }

        public int getN() {
            return n;
        }

        public double getMean() {
            return mean;
        }

        public int getRarity() {
            return rarity;
        }

        public double getPower_old() {
            return power_old;
        }

        public double getProbability_single() {
            return probability_single;
        }

        public double getIntegral_score() {
            return integral_score;
        }

        public double getSignificance() {
            return significance;
        }

        public double getProbability() {
            return probability;
        }

        public int getFILTERING_SIGNIFICANCE() {
            return FILTERING_SIGNIFICANCE;
        }
    }
}



