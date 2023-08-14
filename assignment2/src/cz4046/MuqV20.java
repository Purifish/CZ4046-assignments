package cz4046;

//class MuqV20 extends ThreePrisonersDilemma.Player {
//
//    enum TrustMode {
//        LENIENT, NORMAL, STRICT;
//    }
//
//    int myDefects = 0;
//
//    boolean isClone(int n, int depth, int[] myHistory, int[] oppHistory) {
//        for (; depth > 0; depth--) {
//            if (myHistory[n - depth] != oppHistory[n - depth]) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    int numRecentDefects(int n, int depth, int[] oppHistory) {
//        int defects = 0;
//        for (; depth > 0; depth--) {
//            defects += oppHistory[n - depth];
//        }
//        return defects;
//    }
//
//    int categorizeOpponent(int n, int[] myHistory, int[] oppHistory) {
//        if (isClone(n, n, myHistory, oppHistory)) {
//            return 2; // categorize as clone
//        }
//
//        return 10;
//    }
//
//    int updateCategory(int oppType, int myLastMove, int oppLastMove) {
//        if (oppType == 2 && myLastMove != oppLastMove) {
//            return 10;
//        }
//
//        return oppType;
//    }
//
//    double roundUpTolerance(double percentage) {
//        percentage += 0.0001;
//        double temp = percentage * 10.0;
//        double y = Math.ceil(temp) - temp;
//
//        return percentage + y / 10.0;
//    }
//
//    final int[] opening = new int[]{0, 0, 0, 0, 1, 0};
//
//    int opp1Type = 20;
//    int opp2Type = 20;
//
//    double opp1Tolerance = 0.5;
//    double opp2Tolerance = 0.5;
//
//    int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
//            /*
//                On round 3, check who defected the last 2 rounds and mark them
//             */
//        if (n == 3) {
//            if (oppHistory1[2] == 1 && oppHistory1[1] == 1) {
//                opp1Type = 1;
//            }
//
//            if (oppHistory2[2] == 1 && oppHistory2[1] == 1) {
//                opp2Type = 1;
//            }
//
//            if (opp1Type == 1 || opp2Type == 1) {
//                myDefects++;
//                return 1;
//            }
//        }
//
//        /* defector cooperates twice in a row */
//        if (opp1Type == 1 && oppHistory1[n - 1] == 0 && oppHistory1[n - 2] == 0) {
//            opp1Type = 10; // TODO: devise better way to handle this kind of opponent
//            opp1Tolerance = 0.8;
//        }
//
//        /* defector cooperates twice in a row */
//        if (opp2Type == 1 && oppHistory2[n - 1] == 0 && oppHistory2[n - 2] == 0) {
//            opp2Type = 10;
//            opp2Tolerance = 0.8;
//        }
//
//        if (opp1Type == 1 || opp2Type == 1) {
//            myDefects++;
//            return 1;
//        }
//
//        // opening: 0 0 0 0 1 0
//        if (n < opening.length) {
//            myDefects += opening[n];
//            return opening[n]; // opening phase
//        }
//
//        if (n == opening.length) {
//            /* categorize our opponents once we exit the opening phase */
//            opp1Type = categorizeOpponent(n, myHistory, oppHistory1);
//            opp2Type = categorizeOpponent(n, myHistory, oppHistory2);
//        } else {
//            /* here we check if our previous categorizing of our opponents hold */
//            opp1Type = updateCategory(opp1Type, myHistory[n - 1], oppHistory1[n - 1]);
//            opp2Type = updateCategory(opp2Type, myHistory[n - 1], oppHistory2[n - 1]);
//        }
//
//        // always cooperate with our clones
//        if (opp1Type == 2 && opp2Type == 2) {
//            return 0;
//        }
//
//        int opp1Last5 = numRecentDefects(n, 5, oppHistory1);
//        int opp2Last5 = numRecentDefects(n, 5, oppHistory2);
//        int opp1Last3 = numRecentDefects(n, 3, oppHistory1);
//        int opp2Last3 = numRecentDefects(n, 3, oppHistory2);
//        ThreePrisonersDilemma.MuqPlayer.TrustMode mode;
//
//        /* determine the current trust mode */
//        if (n < 12) {
//            mode = ThreePrisonersDilemma.MuqPlayer.TrustMode.LENIENT; // lenient in the opening
//        } else if (n < 80) {
//            mode = ThreePrisonersDilemma.MuqPlayer.TrustMode.NORMAL; // 'normal' in the middle-game
//        } else {
//            mode = ThreePrisonersDilemma.MuqPlayer.TrustMode.STRICT; // strict in the endgame
//        }
//
//        // cooperate with other clone if 3rd player has an 'acceptable' chance of cooperating
//        if (opp1Type == 2) {
//            if (mode == ThreePrisonersDilemma.MuqPlayer.TrustMode.STRICT) {
//                if (opp2Last3 <= 1) {
//                    return 0;
//                }
//                myDefects++;
//                return 1;
//            }
//            if (opp2Last5 <= 3) {
//                return 0;
//            }
//            myDefects++;
//            return 1;
//        }
//
//        if (opp2Type == 2) {
//            if (mode == ThreePrisonersDilemma.MuqPlayer.TrustMode.STRICT) {
//                if (opp1Last3 <= 1) {
//                    return 0;
//                }
//                myDefects++;
//                return 1;
//            }
//            if (opp1Last5 <= 3) {
//                return 0;
//            }
//            myDefects++;
//            return 1;
//        }
//
//        if (oppHistory1[n - 1] == 1) {
//            double tolerance = roundUpTolerance(1.0 - ((double)myDefects - myHistory[n - 1]) / (n - 1));
//            if (tolerance > opp1Tolerance) {
//                opp1Tolerance = tolerance;
//            }
//        }
//
//        if (oppHistory2[n - 1] == 1) {
//            double tolerance = roundUpTolerance(1.0 - ((double)myDefects - myHistory[n - 1]) / (n - 1));
//            if (tolerance > opp2Tolerance) {
//                opp2Tolerance = tolerance;
//            }
//        }
//
//        boolean opp1IsAlly;
//        boolean opp2IsAlly;
//
//        if (mode == ThreePrisonersDilemma.MuqPlayer.TrustMode.LENIENT) {
//            opp1IsAlly = opp1Last5 <= 3;
//            opp2IsAlly = opp2Last5 <= 3;
//        } else if (mode == ThreePrisonersDilemma.MuqPlayer.TrustMode.NORMAL) {
//            opp1IsAlly = opp1Last5 <= 2 || opp1Last3 <= 1;
//            opp2IsAlly = opp2Last5 <= 2 || opp2Last3 <= 1;
//        } else {
//            if (oppHistory1[n - 1] == 1 && oppHistory2[n - 1] == 1) {
//                myDefects++;
//                return 1;
//            }
//            opp1IsAlly = opp1Last5 <= 1;
//            opp2IsAlly = opp2Last5 <= 1;
//        }
//
//        // if both opponents have shown willingness to cooperate
//        if (opp1IsAlly && opp2IsAlly) {
//            if (myHistory[n - 1] == 0 && (double)(n - myDefects) / (n + 1) >= Math.max(opp1Tolerance, opp2Tolerance)) {
//                myDefects++;
//                return 1;
//            }
//            return 0; // cooperate otherwise
//        }
//
//        myDefects++;
//        return 1; // defect if we can't trust either of our opponents
//    }
//}
