package cz4046;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ThreePrisonersDilemma {
	
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

    /*
        (I cooperate)
        6 3
        3 0

        both coop -> 6, 6, 6 (equal)
        1 coop 1 def -> 3, C: 3, D: 8 (equal with C, -5 with D)
        both def -> 0, 5, 5 (-5)

        (I defect)
        8 5
        5 2

        both coop -> 8, 3, 3 (+5)
        1 coop 1 def -> 5, C: 0, D: 5 (equal with D, +5 with C)
        both def -> 2, 2, 2 (equal)

        ========================

        (Selfish strat)
        opponents possible moves:
                        I Coop      I Def
        CC      ->      0           +5
        CD/DC   ->      0, -5       0, +5
        DD      ->      -5          0

        (Group max strat)
        opponents possible moves:
                        I Coop      I Def
        CC      ->      18          14
        CD/DC   ->      14          10
        DD      ->      10          6


     */

    static int[][][] payoff = {
            {{6, 3},  //payoffs when first and second players cooperate
                    {3, 0}}, //payoffs when first player coops, second defects
            {{8, 5},  //payoffs when first player defects, second coops
                    {5, 2}}};//payoffs when first and second players defect
	
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
            return result.substring(result.indexOf('$') + 1);
        }
    }

    /* Here are four simple strategies: */

    class Huang_KyleJunyuan_Player extends Player {
        // Helper function to calculate percentage of cooperation
        float calCoopPercentage(int[] history) {
            int cooperates = 0;
            int length = history.length;

            for (int i = 0; i < length; i++)
                if (history[i] == 0)
                    cooperates++;

            return (float) cooperates / length * 100;
        }

        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            if (n == 0)
                return 0; // First round: Cooperate

            /* 1. Calculate percentage of cooperation */
            float perOpp1Coop = calCoopPercentage(oppHistory1);
            float perOpp2Coop = calCoopPercentage(oppHistory2);

            /* 2. If both players are mostly cooperating */
            if (perOpp1Coop > 90 && perOpp2Coop > 90) {
                int range = (10 - 5) + 1; // Max: 10, Min: 5
                int random = (int) (Math.random() * range) + 5;

                if (n > (90 + random))  // Selfish: Last min defect
                    return 1;
                else
                    return 0;    // First ~90 rounds: Cooperate
            }

            /* 3. Defect by default */
            return 1;
        }
    }

    // 0 0 0 0 1 1 1 0 0
    // 0 0 0 0 0 0 1 1 1
    class Ngo_Jason_Player extends Player { // extends Player
        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            if (n == 0)
                return 0; // cooperate by default

            if (n >= 109)
                return 1; // opponents cannot retaliate

            // https://www.sciencedirect.com/science/article/abs/pii/S0096300316301011
            if (oppHistory1[n - 1] == oppHistory2[n - 1])
                return oppHistory1[n - 1];

            // n starts at 0, so compare history first

            if (n % 2 != 0) { // odd round - be tolerant
                // TolerantPlayer
                int opponentCoop = 0;
                int opponentDefect = 0;

                for (int i = 0; i < n; i++) {
                    if (oppHistory1[i] == 0)
                        opponentCoop += 1;
                    else
                        opponentDefect += 1;

                    if (oppHistory2[i] == 0)
                        opponentCoop += 1;
                    else
                        opponentDefect += 1;
                }

                return (opponentDefect > opponentCoop) ? 1 : 0;
            }
            // else: even round - compare history

            // HistoryPlayer
            int myNumDefections = 0;
            int oppNumDefections1 = 0;
            int oppNumDefections2 = 0;

            for (int index = 0; index < n; ++index) {
                myNumDefections += myHistory[index];
                oppNumDefections1 += oppHistory1[index];
                oppNumDefections2 += oppHistory2[index];
            }

            if (myNumDefections >= oppNumDefections1 && myNumDefections >= oppNumDefections2)
                return 0;
            else
                return 1;
        }
    }

    class LIM_KAISHENG_Player extends Player {
        // Helper function for TitForTwoTats
        boolean defected_twice(int[] history) {
            int count = 0;
            int length = history.length;

            if (length < 2)
                return false;

            for (int i = length - 2; i < length; i++) {
                if (history[i] == 1) {
                    count++;
                }
            }

            return count == 2;
        }

        // Helper function for SoftMajority
        float cooperate(int[] history) {
            int count = 0;
            int length = history.length;

            for (int i = 0; i < length; i++)
                if (history[i] == 0)
                    count++;

            return (float) count / length;
        }

        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            /* 1. Cooperate in first round */
            if (n == 0)
                return 0;

		/* 2. Closing to the end of the game, defect to maximize rewards.
		Set N to a smaller value assuming most other players will do this trick as well */
            if (n > 90) {
                return 1;
            }

		/* 3. Tit-For-Twice-Tats Strategy
		Cross over to defection once both players had defected twice defecting */
            boolean opp1 = defected_twice(oppHistory1);
            boolean opp2 = defected_twice(oppHistory2);

            if (opp1 && opp2) {
                return 1;
            }

		/* 4. SoftMajority Strategy
		With percentage tuned higher */
            float percentage1 = cooperate(oppHistory1);
            float percentage2 = cooperate(oppHistory2);
            if (percentage1 >= 0.6 && percentage2 >= 0.6) {
                return 0; // Cooperate since opponents are cooperative
            } else {
                return 1; // Defect since opponents are defecting
            }
        }
    }

    class WILSON_TENG_Player extends Player {
        final String NAME = "[██] WILSON_THURMAN_TENG";
        final String MATRIC_NO = "[██] U1820540H";

        int[][][] payoff = {
                {{6, 3},     //payoffs when first and second players cooperate
                        {3, 0}},     //payoffs when first player coops, second defects
                {{8, 5},     //payoffs when first player defects, second coops
                        {5, 2}}};    //payoffs when first and second players defect

        int r;
        int[] myHist, opp1Hist, opp2Hist;
        int myScore = 0, opp1Score = 0, opp2Score = 0;
        int opponent1Coop = 0;
        int opponent2Coop = 0;

        final double LENIENT_THRESHOLD = 0.705; // Used for Law [#1]
        final double STRICT_THRESHOLD = 0.750; // Used for Law [#2]

        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            /**
             LAWS:
             [#0] Unless I am losing, be trustworthy and unpredictable at the same time.
             [#1] Protect myself.
             [#2] Cooperate in a cooperative environment.
             [#3] If I am losing, turn it into a lose-lose situation.
             */

            // Assume environment is cooperative. Always cooperate in first round!
            if (n == 0) return 0;

            // Updating class variables for use in methods.
            this.r = n - 1; // previous round index
            this.myHist = myHistory;
            this.opp1Hist = oppHistory1;
            this.opp2Hist = oppHistory2;

            // Updating Last Actions (LA) for all players.
            int myLA = myHistory[r];
            int opp1LA = oppHistory1[r];
            int opp2LA = oppHistory2[r];

            // Updating Scores for all players
            this.myScore += payoff[myLA][opp1LA][opp2LA];
            this.opp1Score += payoff[opp1LA][opp2LA][myLA];
            this.opp2Score += payoff[opp2LA][opp1LA][myLA];

            // Update opponent's cooperate record.
            if (n > 0) {
                opponent1Coop += oppAction(opp1Hist[r]);
                opponent2Coop += oppAction(opp2Hist[r]);
            }
            // Calculate opponent's cooperate probability.
            double opponent1Coop_prob = opponent1Coop / opp1Hist.length;
            double opponent2Coop_prob = opponent2Coop / opp2Hist.length;

            /** [PROTECT MYSELF]: -> Law [#1]
             When it is nearing the end of the tournament at 100 rounds, if both players are known to be relatively nasty
             (cooperate less than 75% of the time). Defect to protect myself.
             */
            if ((n > 100) && (opponent1Coop_prob < STRICT_THRESHOLD && opponent2Coop_prob < STRICT_THRESHOLD)) {
                // Law [#0] Added
                return actionWithNoise(1, 99);
            }

            /** [REWARD COOPERATION]: -> Law [#2]
             At any point in time before we are able to accurately decide if opponents are nasty or not. We set a lenient
             threshold (0.705) to gauge if opponents are cooperative. Additionally, we check if both opponent's last action
             was to cooperate. If yes, we will cooperate too.
             */
            if ((opp1LA + opp2LA == 0) && (opponent1Coop_prob > LENIENT_THRESHOLD && opponent2Coop_prob > LENIENT_THRESHOLD)) {
                // Law [#0] Added
                return actionWithNoise(0, 99);
            } else
            /** [I WILL NOT LOSE] -> Law [#3]
             However, if opponent is not cooperative, we will check if we have the highest score.
             If we have the highest score, we are appeased and will cooperate. Else, we will defect.
             */
                return SoreLoser();
        }

        /**
         * Law [#0]: This utility method introduces noise to an agent's action, allowing it to be unpredictable.
         *
         * @param intendedAction                     The agent's intended action.
         * @param percent_chance_for_intended_action The percentage chance the agent will perform it's intended action.
         * @return The agent's final action.
         */
        private int actionWithNoise(int intendedAction, int percent_chance_for_intended_action) {
            Map<Integer, Integer> map = new HashMap<Integer, Integer>() {{
                put(intendedAction, percent_chance_for_intended_action);
                put(oppAction(intendedAction), 1 - percent_chance_for_intended_action);
            }};
            LinkedList<Integer> list = new LinkedList<>();
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    list.add(entry.getKey());
                }
            }
            Collections.shuffle(list);
            return list.pop();
        }

        /**
         * Law [#3]:
         * Cooperates if agent currently has the highest score, else defect.
         *
         * @return
         */
        private int SoreLoser() {
            if (iAmWinner()) return 0;
            return 1;
        }

        /* Function to check if agent is loser or not. Agent is a winner if it has the highest score. */
        private boolean iAmWinner() {
            if (myScore >= opp1Score && myScore >= opp2Score) {
                return true;
            }
            return false;
        }

        /* Utility method to obtain opposite action. */
        private int oppAction(int action) {
            if (action == 1) return 0;
            return 1;
        }
    }

    class PM_Low extends Player {

        int myScore = 0;
        int opp1Score = 0;
        int opp2Score = 0;

        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

            if (n == 0) {
                return 0; // cooperate by default
            }

            // get the recent history index
            int i = n - 1;

            // add up the total score/points for each player
            myScore += payoff[myHistory[i]][oppHistory1[i]][oppHistory2[i]];
            opp1Score += payoff[oppHistory1[i]][oppHistory2[i]][myHistory[i]];
            opp2Score += payoff[oppHistory2[i]][myHistory[i]][oppHistory1[i]];

            // if my score is lower than the any of them
            // it means that at least one of them have defected
            if (myScore >= opp1Score && myScore >= opp2Score) {

                // cooperate if my score is higher or equal than all of them
                return 0;
            }

            return 1; // defect if my score is lower than any of them
        }
    }

    class Ultimate_Player extends Player {
        int myScore = 0;
        int opp1Score = 0;
        int opp2Score = 0;

        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {

            if (n == 0) {
                return 0; // cooperate by default
            }

            // get the recent history index
            int i = n - 1;

            // add up the total score/points for each player
            myScore += payoff[myHistory[i]][oppHistory1[i]][oppHistory2[i]];
            opp1Score += payoff[oppHistory1[i]][oppHistory2[i]][myHistory[i]];
            opp2Score += payoff[oppHistory2[i]][myHistory[i]][oppHistory1[i]];

            if (n < 3) {
                return 0; // cooperate by default
            }


            // if my score is lower than any of them
            // it means that at least one of them have defected
            if (myScore >= opp1Score && myScore >= opp2Score) {
                if (oppHistory1[n - 1] + oppHistory1[n - 2] + oppHistory1[n - 3] == 0
                        && oppHistory2[n - 1] + oppHistory2[n - 2] + oppHistory2[n - 3] == 0
                        && myHistory[n - 1] + myHistory[n - 2] + myHistory[n - 3] == 0) {
                    return 1;
                }
                return 0;
            }

            return 1; // defect if my score is lower than any of them
        }
    }

    class OldMuqPlayer extends Player {
        private boolean opp1IsClone = false;
        private boolean opp2IsClone = false;

        boolean isClone(int n, int[] myHistory, int[] oppHistory) {
            return myHistory[n - 1] == oppHistory[n - 1] && myHistory[n - 2] == oppHistory[n - 2] && myHistory[n - 3] == oppHistory[n - 3]
                    && myHistory[n - 4] == oppHistory[n - 4] && myHistory[n - 5] == oppHistory[n - 5];
        }

        int selectAction(int n, int[] myHistory, int[] oppHistory1, int[] oppHistory2) {
            if (n < 4) {
                return 0; // coop for 4 turns
            }

            if (n == 4) {
                return 1; // defect on 5th turn
            }

            if (n == 5) {
                opp1IsClone = isClone(5, myHistory, oppHistory1);
                opp2IsClone = isClone(5, myHistory, oppHistory2);
            } else if (n % 5 == 0) {
                if (opp1IsClone && !isClone(n, myHistory, oppHistory1)) {
                    opp1IsClone = false;
                }

                if (opp2IsClone && !isClone(n, myHistory, oppHistory2)) {
                    opp2IsClone = false;
                }
            }

            return opp1IsClone || opp2IsClone ? 0 : 1;
        }
    }

    class MuqPlayer extends Player {

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

        int decideWithClone(TrustMode mode, int oppLast3, int oppLast5){
            if (mode == TrustMode.STRICT) {
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

        TrustMode getTrustMode(int rounds) {
            if (rounds < opening.length + 4) {
                return TrustMode.LENIENT; // lenient in the opening
            } else if (rounds < 80) {
                return TrustMode.NORMAL; // 'normal' in the middle-game
            } else {
                return TrustMode.STRICT; // strict in the endgame
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
            TrustMode mode = getTrustMode(n);

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
            if (mode == TrustMode.LENIENT) {
                allyIsTrusted[0] = oppLast5[0] <= 3;
                allyIsTrusted[1] = oppLast5[1] <= 3;
            } else if (mode == TrustMode.NORMAL) {
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
            for (int i = 0; i < n; i++) {
                if (oppHistory1[i] == 0)
                    opponentCoop = opponentCoop + 1;
                else
                    opponentDefect = opponentDefect + 1;
            }
            for (int i = 0; i < n; i++) {
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
            if (n == 0) return 0; //cooperate by default
            if (Math.random() < 0.5)
                return oppHistory1[n - 1];
            else
                return oppHistory2[n - 1];
        }
    }


    /* In our tournament, each pair of strategies will play one match against each other.
     This procedure simulates a single match and returns the scores. */
    float[] scoresOfMatch(Player A, Player B, Player C, int rounds) {
        int[] HistoryA = new int[0], HistoryB = new int[0], HistoryC = new int[0];
        float ScoreA = 0, ScoreB = 0, ScoreC = 0;

        for (int i = 0; i < rounds; i++) {
            int PlayA = A.selectAction(i, HistoryA, HistoryB, HistoryC);
            int PlayB = B.selectAction(i, HistoryB, HistoryC, HistoryA);
            int PlayC = C.selectAction(i, HistoryC, HistoryA, HistoryB);
            ScoreA = ScoreA + payoff[PlayA][PlayB][PlayC];
            ScoreB = ScoreB + payoff[PlayB][PlayC][PlayA];
            ScoreC = ScoreC + payoff[PlayC][PlayA][PlayB];
            HistoryA = extendIntArray(HistoryA, PlayA);
            HistoryB = extendIntArray(HistoryB, PlayB);
            HistoryC = extendIntArray(HistoryC, PlayC);

//            if (A instanceof MuqPlayer && B instanceof LIM_KAISHENG_Player && C instanceof LIM_KAISHENG_Player) {
//                System.out.printf("%d %d\n", PlayA, PlayB);
//            }
        }
        float[] result = {ScoreA / rounds, ScoreB / rounds, ScoreC / rounds};
        return result;
    }

    //	This is a helper function needed by scoresOfMatch.
    int[] extendIntArray(int[] arr, int next) {
        int[] result = new int[arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i];
        }
        result[result.length - 1] = next;
        return result;
    }
	
	/* The procedure makePlayer is used to reset each of the Players 
	 (strategies) in between matches. When you add your own strategy,
	 you will need to add a new entry to makePlayer, and change numPlayers.*/

    //    int numPlayers = 6;
    int numPlayers = 11;

    Player makePlayer(int which) {
        switch (which) {
            case 0:
                return new NastyPlayer();
            case 1:
                return new TolerantPlayer();
            case 2:
                return new T4TPlayer();
            case 3:
                return new MuqPlayer();
            case 4:
                return new LIM_KAISHENG_Player();
            case 5:
                return new Ngo_Jason_Player();
            case 6:
                return new WILSON_TENG_Player();
            case 7:
                return new PM_Low();
            case 8:
                return new Huang_KyleJunyuan_Player();
            case 9:
                return new RandomPlayer();
            case 10:
                return new NicePlayer();
            case 11:
                return new Ultimate_Player();
            case 12:
                return new OldMuqPlayer();
            case 13:
                return new FreakyPlayer();
        }
        throw new RuntimeException("Bad argument passed to makePlayer");
    }

    /* Finally, the remaining code actually runs the tournament. */

    public static void main(String[] args) {
        ThreePrisonersDilemma instance = new ThreePrisonersDilemma();
        instance.runTournament();
    }

    boolean verbose = true; // set verbose = false if you get too much text output

    void runTournament() {
        float[] totalScore = new float[numPlayers];

        // This loop plays each triple of players against each other.
        // Note that we include duplicates: two copies of your strategy will play once
        // against each other strategy, and three copies of your strategy will play once.

//        Player X = makePlayer(3); // Create a fresh copy of each player
//        Player Y = makePlayer(4);
//        Player Z = makePlayer(4);
//        int rd = 90 + (int) Math.rint(20 * Math.random()); // Between 90 and 110 rounds
//        float[] mr = scoresOfMatch(X, Y, Z, rd); // Run match
//        totalScore[3] = totalScore[3] + mr[0];
//        totalScore[4] = totalScore[4] + mr[1];
//        totalScore[4] = totalScore[4] + mr[2];
//        System.out.println(X.name() + " scored " + mr[0] +
//                                " points, " + Y.name() + " scored " + mr[1] +
//                                " points, and " + Z.name() + " scored " + mr[2] + " points.");


        for (int i = 0; i < numPlayers; i++)
            for (int j = i; j < numPlayers; j++)
                for (int k = j; k < numPlayers; k++) {

                    Player A = makePlayer(i); // Create a fresh copy of each player
                    Player B = makePlayer(j);
                    Player C = makePlayer(k);
                    int rounds = 90 + (int) Math.rint(20 * Math.random()); // Between 90 and 110 rounds
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
        for (int i = 0; i < numPlayers; i++) {
            int j = i - 1;
            for (; j >= 0; j--) {
                if (totalScore[i] > totalScore[sortedOrder[j]])
                    sortedOrder[j + 1] = sortedOrder[j];
                else break;
            }
            sortedOrder[j + 1] = i;
        }

        // Finally, print out the sorted results.
        if (verbose) System.out.println();
        System.out.println("Tournament Results");
        for (int i = 0; i < numPlayers; i++)
            System.out.println(makePlayer(sortedOrder[i]).name() + ": "
                    + totalScore[sortedOrder[i]] + " points.");

    } // end of runTournament()

} // end of class PrisonersDilemma
