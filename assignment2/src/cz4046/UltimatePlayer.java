package cz4046;

//class MuqPlayer extends ThreePrisonersDilemma.Player {
//
//    final int[] opening = new int[]{0, 0, 0, 1, 1, 0};
//    final int openingLength = opening.length;
//
//    int opp1Type = -1;
//    int opp2Type = -1;
//    boolean opp1IsClone = false;
//    boolean opp2IsClone = false;
//    boolean isClone(int n, int depth, int[] myHistory, int[] oppHistory) {
//        for (;depth > 0; depth--) {
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
//    int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
//        if (n < 2) {
//            return 0; // cooperate first for a while
//        }
//
//        if (n == 2) {
//            return 1; // then suddenly defect
//        }
//
//        if (n == 3) {
//            opp1IsClone = isClone(3, 3, myHistory, oppHistory1);
//            opp2IsClone = isClone(3, 3, myHistory, oppHistory2);
//        } else {
//            if (opp1IsClone && myHistory[n - 1] != oppHistory1[n - 1]) {
//                opp1IsClone = false;
//            }
//
//            if (opp2IsClone && myHistory[n - 1] != oppHistory2[n - 1]) {
//                opp2IsClone = false;
//            }
//        }
//
//        // always cooperate with our clones for maximum rewards
//        if (opp1IsClone && opp2IsClone) {
//            return 0;
//        }
//
//        if (n >= 90) {
//            return 1; // defect for last few rounds
//        }
//
//        if (oppHistory1[n - 1] == 1 && oppHistory1[n - 2] == 1 && oppHistory2[n - 1] == 1 && oppHistory2[n - 2] == 1) {
//            return 1;
//        }
//
//        boolean opp1IsAlly;
//        boolean opp2IsAlly;
//
//        if (n <= 5) {
//            opp1IsAlly = numRecentDefects(n, 3, oppHistory1) < 3;
//            opp2IsAlly = numRecentDefects(n, 3, oppHistory2) < 3;
//        } else {
//            opp1IsAlly = numRecentDefects(n, 5, oppHistory1) <= 2 || numRecentDefects(n, 3, oppHistory1) <= 1;
//            opp2IsAlly = numRecentDefects(n, 5, oppHistory2) <= 2 || numRecentDefects(n, 3, oppHistory2) <= 1;
//        }
//
//        // cooperate with other clone if 3rd player has a decent chance of cooperating
//        if (opp1IsClone) {
//            return opp2IsAlly ? 0 : 1;
//        }
//
//        if (opp2IsClone) {
//            return opp1IsAlly ? 0 : 1;
//        }
//
//        if (n > 5) {
//            opp1IsAlly = numRecentDefects(n, 5, oppHistory1) <= 3;
//            opp2IsAlly = numRecentDefects(n, 5, oppHistory2) <= 3;
//        }
//
//        // TODO: Make more strict to cooperate with 2 non-clones/
//        if (opp1IsAlly && opp2IsAlly) {
//            return 0;
//        }
//
//        return 1;
//    }
//}

//class MuqPlayer extends ThreePrisonersDilemma.Player {
//
//    // TODO: add 1 more 1 to avoid misclassifying fools ?
//    // final int[] opening = new int[]{0, 0, 0, 1, 1, 0, 0};
//    final int[] opening = new int[]{0, 0, 0, 0, 1, 1, 1, 0, 0};
//    // 0 0 0 0 1 1 1 0 0 0 0 1 0
//    // 0 0 0 0 0 0 0 1 0 0 0 0
//
//    int opp1Type = -1;
//    int opp2Type = -1;
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
//        int defects = numRecentDefects(n, n, oppHistory);
//        if (defects == n) {
//            return 1; // categorize as pure-defector (untrusting)
//        }
//
//        if (defects == 0) {
//            return 0; // categorize as pure-cooperator (fool)
//        }
//
//        return -1;
//    }
//
//    int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
//        if (n < opening.length) {
//            return opening[n]; // opening phase
//        }
//
//        if (n == opening.length) {
//            /* categorize our opponents once we exit the opening phase */
//            opp1Type = categorizeOpponent(n, myHistory, oppHistory1);
//            opp2Type = categorizeOpponent(n, myHistory, oppHistory2);
//        } else {
//            /* here we check if our previous categorizing of our opponents hold */
//            if (opp1Type == 2 && myHistory[n - 1] != oppHistory1[n - 1]
//                    || opp1Type == 0 && oppHistory1[n - 1] != 0
//                    || opp1Type == 1 && oppHistory1[n - 1] != 1) {
//                opp1Type = -1; // un-categorize the player if assumption was wrong
//            }
//
//            if (opp2Type == 2 && myHistory[n - 1] != oppHistory2[n - 1]
//                    || opp2Type == 0 && oppHistory2[n - 1] != 0
//                    || opp2Type == 1 && oppHistory2[n - 1] != 1) {
//                opp2Type = -1; // un-categorize the player if assumption was wrong
//            }
//        }
//
//        // always cooperate with our clones
//        if (opp1Type == 2 && opp2Type == 2) {
//            return 0;
//        }
//
//        // always defect if both opponents always cooperate
//        if (opp1Type == 0 && opp2Type == 0) {
//            return 1;
//        }
//
//        // always defect if either opponent always defects
//        if (opp1Type == 1 || opp2Type == 1) {
//            return 1;
//        }
//
////            // defect for last few rounds
////            if (n >= 90) {
////                return 1;
////            }
//
//        int opp1Last5 = numRecentDefects(n, 5, oppHistory1);
//        int opp2Last5 = numRecentDefects(n, 5, oppHistory2);
//
//        // cooperate with other clone if 3rd player has a decent chance of cooperating
//        if (opp1Type == 2) {
//            return opp2Last5 <= 3 ? 0 : 1;
//        }
//
//        if (opp2Type == 2) {
//            return opp1Last5 <= 3 ? 0 : 1;
//        }
//
//        int opp1Last3 = numRecentDefects(n, 3, oppHistory1);
//        int opp2Last3 = numRecentDefects(n, 3, oppHistory2);
//
//        boolean opp1IsAlly = opp1Last5 <= 3 || opp1Last3 <= 1;
//        boolean opp2IsAlly = opp2Last5 <= 3 || opp2Last3 <= 1;
//
//        // if both opponents have shown willingness to cooperate
//        if (opp1IsAlly && opp2IsAlly) {
//            // if both opponents have gained our trust
//            if (opp1Last3 == 0 && opp2Last3 == 0 && myHistory[n - 1] + myHistory[n - 2] + myHistory[n - 3] == 0) {
//                return 1; // defect only every 4 rounds to (hopefully) maintain their trust
//            }
//            return 0; // cooperate otherwise
//        }
//
//        return 1; // defect if we can't trust either of our opponents
//    }
//}


//class MuqPlayerV10 extends ThreePrisonersDilemma.Player {
//
//    enum TrustMode {
//        LENIENT, NORMAL, STRICT;
//    }
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
//        int defects = numRecentDefects(n, n, oppHistory);
//
//        if (defects == n) {
//            return 1; // categorize as pure-defector (untrusting)
//        }
//
//        if (defects == 0) {
//            return 0; // categorize as pure-cooperator (fool)
//        }
//
//        int earlyDefects = oppHistory[0] + oppHistory[1] + oppHistory[2] + oppHistory[3] + oppHistory[4];
//
//        if (earlyDefects >= 3) {
//            return 20; // non-ally (deemed unwilling to cooperate)
//        }
//
//        // 0 0 0 0 0 [1 1 1 0 0]
//        // 0 0 0 0 0 [0 1 1 1  ]
//
//        // determine trusting-ness of opponent (10 to 15, with 10 as most trusting)
//        return defects - earlyDefects + 10;
//    }
//
//    int updateCategory(int oppType, int myLastMove, int oppLastMove) {
//        if (oppType == 1 && oppLastMove != 1) {
//            return 20;
//        }
//
//        if (oppType == 0 && oppLastMove != 0) {
//            return 10;
//        }
//
//        if (oppType == 2 && myLastMove != oppLastMove) {
//            return 12;
//        }
//
//        return oppType;
//    }
//
//    final int[] opening = new int[]{0, 0, 0, 0, 0, 1, 1, 0, 0};
//    // final int[] opening = new int[]{0, 0, 0, 0, 1, 0};
//    // 0 0 0 0 1 0 1 0 1 0
//    // 0 0 0 0 0 0 0 1
//
//    // final int[] opening = new int[]{0, 0, 0, 0, 1, 1, 1, 0, 0};
//    // TODO: check earlier for nasty player (e.g. at round 3, check who defected rounds 0 to 2)
//
//    int opp1Type = 20;
//    int opp2Type = 20;
//
//    double opp1Tolerance = 50.0;
//    double opp2Tolerance = 50.0;
//
//    int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
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
//                return 1;
//            }
//        }
//
//        // opening: 0 0 0 0 1 0
//        if (n < opening.length) {
//            if (opp1Type == 1) {
//                opp1Type = updateCategory(opp1Type, myHistory[n - 1], oppHistory1[n - 1]);
//            }
//            if (opp2Type == 1) {
//                opp2Type = updateCategory(opp2Type, myHistory[n - 1], oppHistory2[n - 1]);
//            }
//
//            if (opp1Type == 1 || opp2Type == 1) {
//                return 1;
//            }
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
////            // always defect if both opponents always cooperate
////            if (opp1Type == 0 && opp2Type == 0) {
////                return 1;
////            }
//
//        // always defect if either opponent always defects
//        if (opp1Type == 1 || opp2Type == 1) {
//            return 1;
//        }
//
//        int opp1Last5 = numRecentDefects(n, 5, oppHistory1);
//        int opp2Last5 = numRecentDefects(n, 5, oppHistory2);
//        int opp1Last3 = numRecentDefects(n, 3, oppHistory1);
//        int opp2Last3 = numRecentDefects(n, 3, oppHistory2);
//        ThreePrisonersDilemma.MuqPlayer.TrustMode mode;
//
//        /* determine the current trust mode */
//        if (n < 15) {
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
//                return opp2Last3 <= 1 ? 0 : 1;
//            }
//            return opp2Last5 <= 3 ? 0 : 1;
//        }
//
//        if (opp2Type == 2) {
//            if (mode == ThreePrisonersDilemma.MuqPlayer.TrustMode.STRICT) {
//                return opp1Last3 <= 1 ? 0 : 1;
//            }
//            return opp1Last5 <= 3 ? 0 : 1;
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
//                return 1;
//            }
//            opp1IsAlly = opp1Last5 <= 1;
//            opp2IsAlly = opp2Last5 <= 1;
//        }
//
//        // if both opponents have shown willingness to cooperate
//        if (opp1IsAlly && opp2IsAlly) {
//            int cycleLength;
//
//            if (opp1Type >= 13 || opp2Type >= 13) {
//                cycleLength = 5;
//            } else if (opp1Type == 12 || opp2Type == 12) {
//                cycleLength = 4;
//            } else {
//                cycleLength = 3;
//            }
//
//                /*
//                    defect every cycleLength turns to abuse their trust
//                    cycleLength has appropriate value to (hopefully) maintain their trust
//                 */
//            if (recentFullCooperation(n, cycleLength, myHistory, oppHistory1, oppHistory2)) {
//                return 1;
//            }
//            return 0; // cooperate otherwise
//        }
//
//        return 1; // defect if we can't trust either of our opponents
//    }
//
//    boolean recentFullCooperation(int n, int depth, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
//        for (int i = 1; i < depth; i++) {
//            if (myHistory[n - i] == 1 || oppHistory1[n - i] == 1 || oppHistory2[n - i] == 1) {
//                return false;
//            }
//        }
//        return true;
//    }
//}