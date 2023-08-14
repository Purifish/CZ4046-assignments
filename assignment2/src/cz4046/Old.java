package cz4046;

public class Old {

	/*
	 This Java program models the two-player Prisoner's Dilemma game.
	 We use the integer "0" to represent cooperation, and "1" to represent
	 defection.

	 Recall that in the 2-players dilemma, U(DC) > U(CC) > U(DD) > U(CD), where
	 we give the payoff for the first player in the list. We want the three-player game
	 to resemble the 2-player game whenever one player's response is fixed, and we
	 also want symmetry, so U(CCD) = U(CDC) etc. This gives the unique ordering

	 U(DCC) > U(CCC) > U(DDC) > U(CDC) > U(DDD) > U(CDD)

	 The payoffs for player 1 are given by the following matrix: */

    static int[][][] payoff = {
            {{6,3},  //payoffs when first and second players cooperate
                    {3,0}}, //payoffs when first player coops, second defects
            {{8,5},  //payoffs when first player defects, second coops
                    {5,2}}};//payoffs when first and second players defect

	/*
	 So payoff[i][j][k] represents the payoff to player 1 when the first
	 player's action is i, the second player's action is j, and the
	 third player's action is k.

	 In this simulation, triples of players will play each other repeatedly in a
	 'match'. A match consists of about 100 rounds, and your score from that match
	 is the average of the payoffs from each round of that match. For each round, your
	 strategy is given a list of the previous plays (so you can remember what your
	 opponent did) and must compute the next action.  */


    abstract class Player {
        // This procedure takes in the number of rounds elapsed so far (n), and
        // the previous plays in the match, and returns the appropriate action.
        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            throw new RuntimeException("You need to override the selectAction method.");
        }

        // Used to extract the name of this player class.
        final String name() {
            String result = getClass().getName();
            return result.substring(result.indexOf('$')+1);
        }
    }

    /* Here are four simple strategies: */

    class NicePlayer extends Player {
        //NicePlayer always cooperates
        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            return 0;
        }
    }

    class NastyPlayer extends Player {
        //NastyPlayer always defects
        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            return 1;
        }
    }

    class RandomPlayer extends Player {
        //RandomPlayer randomly picks his action each time
        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            if (Math.random() < 0.5)
                return 0;  //cooperates half the time
            else
                return 1;  //defects half the time
        }
    }

    class TolerantPlayer extends Player {
        //TolerantPlayer looks at his opponents' histories, and only defects
        //if at least half of the other players' actions have been defects
        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            int opponentCoop = 0;
            int opponentDefect = 0;
            for (int i=0; i<n; i++) {
                if (oppHistory1[i] == 0)
                    opponentCoop = opponentCoop + 1;
                else
                    opponentDefect = opponentDefect + 1;
            }
            for (int i=0; i<n; i++) {
                if (oppHistory2[i] == 0)
                    opponentCoop = opponentCoop + 1;
                else
                    opponentDefect = opponentDefect + 1;
            }
            if (opponentDefect > opponentCoop)
                return 1;
            else
                return 0;
        }
    }

    class FreakyPlayer extends Player {
        //FreakyPlayer determines, at the start of the match,
        //either to always be nice or always be nasty.
        //Note that this class has a non-trivial constructor.
        int action;
        FreakyPlayer() {
            if (Math.random() < 0.5)
                action = 0;  //cooperates half the time
            else
                action = 1;  //defects half the time
        }

        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            return action;
        }
    }

    class T4TPlayer extends Player {
        //Picks a random opponent at each play,
        //and uses the 'tit-for-tat' strategy against them
        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            if (n==0) return 0; //cooperate by default
            if (Math.random() < 0.5)
                return oppHistory1[n-1];
            else
                return oppHistory2[n-1];
        }
    }

    class MuqaffaPlayer extends Player {

        /**
         * Indicates how lenient this player is in trusting a foe.
         * LENIENT is the most lenient mode, while STRICT is the least lenient.
         * */
        enum TrustMode {
            LENIENT, NORMAL, STRICT;
        }

        /**
         * Categorizes a foe based on their opening moves.
         * @param n The current number of rounds
         * @param depth The number of past moves to look at
         * @return 2 if foe is a 'clone', 10 if they are an 'ally', 30 if they are 'rogue'
         */
        int categorizePlayer(int n, int depth, int[] myHistory, int[] oppHistory) {
            int defects = 0;
            boolean isClone = true;

            for (; depth > 0; depth--) {
                if (myHistory[n - depth] != oppHistory[n - depth]) {
                    isClone = false;
                }
                defects += oppHistory[n - depth];
            }

            if (isClone) {
                return 2;
            }

            return defects <= 3 ? 10 : 30;
        }

        int numRecentDefects(int n, int depth, int[] oppHistory) {
            int defects = 0;
            for (; depth > 0; depth--) {
                defects += oppHistory[n - depth];
            }
            return defects;
        }

        double roundUpTolerance(double percentage) {
            return Math.ceil(percentage * 20.0 + 0.00001) / 20.0;
        }

        int decideWithClone(ThreePrisonersDilemma.MuqPlayer.TrustMode mode, int oppLast3, int oppLast5){
            if (mode == ThreePrisonersDilemma.MuqPlayer.TrustMode.STRICT) {
                if (oppLast3 <= 1) {
                    return 0;
                }
                return 1;
            }
            if (oppLast5 <= 3) {
                return 0;
            }
            return 1;
        }

        void updateTolerance(int opp1LastMove, int opp2LastMove, int myLastMove, int myDefects, int n) {
            double tolerance = roundUpTolerance(1.0 - ((double)myDefects - myLastMove) / (n - 1));
            if (opp1LastMove == 1) {
                opp1Tolerance = Math.max(opp1Tolerance, tolerance);
            }

            if (opp2LastMove == 1) {
                opp2Tolerance = Math.max(opp2Tolerance, tolerance);
            }
        }

        ThreePrisonersDilemma.MuqPlayer.TrustMode getTrustMode(int rounds) {
            if (rounds < opening.length + 4) {
                return ThreePrisonersDilemma.MuqPlayer.TrustMode.LENIENT; // lenient in the opening
            } else if (rounds < 80) {
                return ThreePrisonersDilemma.MuqPlayer.TrustMode.NORMAL; // 'normal' in the middle-game
            } else {
                return ThreePrisonersDilemma.MuqPlayer.TrustMode.STRICT; // strict in the endgame
            }
        }

        // final int[] opening = new int[]{0, 0, 0, 0, 1, 0};
        final int[] opening = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0};

        int opp1Type = 0;
        int opp2Type = 0;

        double opp1Tolerance = 0.5;
        double opp2Tolerance = 0.5;

        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            /* Cooperate on the first few rounds to gain trust */
            if (n < 3) {
                return opening[n];
            }

            /* On round 3, check for any rogues */
            if (n == 3) {
                if (oppHistory1[n - 2] == 1 && oppHistory1[n - 1] == 1) {
                    opp1Type = 30; // mark as rogue
                }

                if (oppHistory2[n - 2] == 1 && oppHistory2[n - 1] == 1) {
                    opp2Type = 30; // mark as rogue
                }
            }

            /* If either foe is a rogue, we cooperate only if both foes cooperated last round */
            if (opp1Type == 30 || opp2Type == 30) {
                return oppHistory1[n - 1] == 0 && oppHistory2[n - 1] == 0 ? 0 : 1;
            }

            /* Play our opening moves while we are in the opening phase, unless there is a rogue */
            if (n < opening.length) {
                if (oppHistory1[n - 2] == 1 && oppHistory1[n - 1] == 1) {
                    opp1Type = 30; // new rogue detected
                    return 1;
                }

                if (oppHistory2[n - 2] == 1 && oppHistory2[n - 1] == 1) {
                    opp2Type = 30; // new rogue detected
                    return 1;
                }
                return opening[n];
            }

            if (n == opening.length) {
                /* Check for clones or new rogues once we exit the opening phase */
                opp1Type = categorizePlayer(n, n, myHistory, oppHistory1);
                opp2Type = categorizePlayer(n, n, myHistory, oppHistory2);
            } else {
                /* Here we check if our previously classification of clones still hold */
                if (opp1Type == 2 && myHistory[n - 1] != oppHistory1[n - 1]) {
                    opp1Type = 10; // reclassify as ally if it doesn't hold
                    opp1Tolerance = 0.6;
                }
                if (opp2Type == 2 && myHistory[n - 1] != oppHistory2[n - 1]) {
                    opp2Type = 10; // reclassify as ally if it doesn't hold
                    opp2Tolerance = 0.6;
                }
            }

            /* Always cooperate with our clones */
            if (opp1Type == 2 && opp2Type == 2) {
                return 0;
            }

            /* If at least one rogue, use a modified tit-for-tat */
            if (opp1Type == 30 || opp2Type == 30) {
                return oppHistory1[n - 1] == 0 && oppHistory2[n - 1] == 0 ? 0 : 1;
            }

            /* Get recent defects data of our opponents */
            int[] oppLast3 = {numRecentDefects(n, 3, oppHistory1), numRecentDefects(n, 3, oppHistory2)};
            int[] oppLast5 = {numRecentDefects(n, 5, oppHistory1), numRecentDefects(n, 5, oppHistory2)};

            /* Determine the current trust mode */
            ThreePrisonersDilemma.MuqPlayer.TrustMode mode = getTrustMode(n);

            /* Cooperate with other clone if the 3rd player has an 'acceptable' chance of cooperating */
            if (opp1Type == 2) {
                return decideWithClone(mode, oppLast3[1], oppLast5[1]);
            }
            if (opp2Type == 2) {
                return decideWithClone(mode, oppLast3[0], oppLast5[0]);
            }

            int myDefects = numRecentDefects(n, n, myHistory);
            boolean[] allyIsTrusted = new boolean[2];

            /* Update the tolerance of our allies if required */
            updateTolerance(oppHistory1[n - 1], oppHistory2[n - 1], myHistory[n - 1], myDefects, n);

            /* Check if each ally can be trusted, based on the current trust mode and their recent moves */
            if (mode == ThreePrisonersDilemma.MuqPlayer.TrustMode.LENIENT) {
                allyIsTrusted[0] = oppLast5[0] <= 3;
                allyIsTrusted[1] = oppLast5[1] <= 3;
            } else if (mode == ThreePrisonersDilemma.MuqPlayer.TrustMode.NORMAL) {
                allyIsTrusted[0] = oppLast5[0] <= 2 || oppLast3[0] <= 1;
                allyIsTrusted[1] = oppLast5[1] <= 2 || oppLast3[1] <= 1;
            } else {
                if (oppHistory1[n - 1] == 1 && oppHistory2[n - 1] == 1) {
                    return 1; // in STRICT mode, always defect if both allies just defected
                }
                allyIsTrusted[0] = oppLast5[0] <= 1;
                allyIsTrusted[1] = oppLast5[1] <= 1;
            }

            /* If both allies are trusted for this round */
            if (allyIsTrusted[0] && allyIsTrusted[1]) {
                /* If within both allies' tolerance levels, and we cooperated last round, defect */
                if (myHistory[n - 1] == 0 && (double)(n - myDefects) / (n + 1) >= Math.max(opp1Tolerance, opp2Tolerance)) {
                    return 1;
                }
                return 0; // cooperate by default
            }

            return 1; // defect if we can't trust either of our allies this round
        }
    }


    /* In our tournament, each pair of strategies will play one match against each other.
     This procedure simulates a single match and returns the scores. */
    float[] scoresOfMatch(Player A, Player B, Player C, int rounds) {
        int[] HistoryA = new int[0], HistoryB = new int[0], HistoryC = new int[0];
        float ScoreA = 0, ScoreB = 0, ScoreC = 0;

        for (int i=0; i<rounds; i++) {
            int PlayA = A.selectAction(i, HistoryA, HistoryB, HistoryC);
            int PlayB = B.selectAction(i, HistoryB, HistoryC, HistoryA);
            int PlayC = C.selectAction(i, HistoryC, HistoryA, HistoryB);
            ScoreA = ScoreA + payoff[PlayA][PlayB][PlayC];
            ScoreB = ScoreB + payoff[PlayB][PlayC][PlayA];
            ScoreC = ScoreC + payoff[PlayC][PlayA][PlayB];
            HistoryA = extendIntArray(HistoryA, PlayA);
            HistoryB = extendIntArray(HistoryB, PlayB);
            HistoryC = extendIntArray(HistoryC, PlayC);
        }
        float[] result = {ScoreA/rounds, ScoreB/rounds, ScoreC/rounds};
        return result;
    }

    //	This is a helper function needed by scoresOfMatch.
    int[] extendIntArray(int[] arr, int next) {
        int[] result = new int[arr.length+1];
        for (int i=0; i<arr.length; i++) {
            result[i] = arr[i];
        }
        result[result.length-1] = next;
        return result;
    }

	/* The procedure makePlayer is used to reset each of the Players
	 (strategies) in between matches. When you add your own strategy,
	 you will need to add a new entry to makePlayer, and change numPlayers.*/

    int numPlayers = 7;
    Player makePlayer(int which) {
        switch (which) {
            case 0: return new NicePlayer();
            case 1: return new NastyPlayer();
            case 2: return new RandomPlayer();
            case 3: return new TolerantPlayer();
            case 4: return new FreakyPlayer();
            case 5: return new T4TPlayer();
            case 6: return new MuqaffaPlayer();
        }
        throw new RuntimeException("Bad argument passed to makePlayer");
    }

    /* Finally, the remaining code actually runs the tournament. */

    public static void main (String[] args) {
        Old instance = new Old();
        instance.runTournament();
    }

    boolean verbose = true; // set verbose = false if you get too much text output

    void runTournament() {
        float[] totalScore = new float[numPlayers];

        // This loop plays each triple of players against each other.
        // Note that we include duplicates: two copies of your strategy will play once
        // against each other strategy, and three copies of your strategy will play once.

        for (int i=0; i<numPlayers; i++) for (int j=i; j<numPlayers; j++) for (int k=j; k<numPlayers; k++) {

            Player A = makePlayer(i); // Create a fresh copy of each player
            Player B = makePlayer(j);
            Player C = makePlayer(k);
            int rounds = 90 + (int)Math.rint(20 * Math.random()); // Between 90 and 110 rounds
            float[] matchResults = scoresOfMatch(A, B, C, rounds); // Run match
            totalScore[i] = totalScore[i] + matchResults[0];
            totalScore[j] = totalScore[j] + matchResults[1];
            totalScore[k] = totalScore[k] + matchResults[2];
            if (verbose)
                System.out.println(A.name() + " scored " + matchResults[0] +
                        " points, " + B.name() + " scored " + matchResults[1] +
                        " points, and " + C.name() + " scored " + matchResults[2] + " points.");
        }
        int[] sortedOrder = new int[numPlayers];
        // This loop sorts the players by their score.
        for (int i=0; i<numPlayers; i++) {
            int j=i-1;
            for (; j>=0; j--) {
                if (totalScore[i] > totalScore[sortedOrder[j]])
                    sortedOrder[j+1] = sortedOrder[j];
                else break;
            }
            sortedOrder[j+1] = i;
        }

        // Finally, print out the sorted results.
        if (verbose) System.out.println();
        System.out.println("Tournament Results");
        for (int i=0; i<numPlayers; i++)
            System.out.println(makePlayer(sortedOrder[i]).name() + ": "
                    + totalScore[sortedOrder[i]] + " points.");

    } // end of runTournament()

} // end of class PrisonersDilemma

