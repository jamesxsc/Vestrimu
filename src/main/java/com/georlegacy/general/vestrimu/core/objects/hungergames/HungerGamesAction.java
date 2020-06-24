package com.georlegacy.general.vestrimu.core.objects.hungergames;

import com.georlegacy.general.vestrimu.core.objects.hungergames.HungerGamesActionResult.ResultType;
import com.georlegacy.general.vestrimu.core.objects.hungergames.HungerGamesActionResult.ResultType.ResultTypeCombo;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class HungerGamesAction {

    @Getter
    private final HungerGamesTribute tribute;

    @Getter
    private final ActionType actionType;

    @Getter
    private final HungerGamesGame.Location location;

    private static final Map<ActionType.ActionTypeCombo, ResultTypeCombo> actionDictionary = new HashMap<ActionType.ActionTypeCombo, ResultTypeCombo>() {{
        put(new ActionType.ActionTypeCombo(ActionType.NONE, ActionType.NONE), new ResultTypeCombo(ResultType.NONE, ResultType.NONE));
        put(new ActionType.ActionTypeCombo(ActionType.NONE, ActionType.APPROACH_CORNUCOPIA), new ResultTypeCombo(ResultType.NONE, ResultType.NONE));
        put(new ActionType.ActionTypeCombo(ActionType.NONE, ActionType.BARE_ATTACK), new ResultTypeCombo(ResultType.INJURY_SEVERE, ResultType.NONE));
        put(new ActionType.ActionTypeCombo(ActionType.NONE, ActionType.RUN), new ResultTypeCombo(ResultType.NONE, ResultType.NONE));
        put(new ActionType.ActionTypeCombo(ActionType.APPROACH_CORNUCOPIA, ActionType.NONE), new ResultTypeCombo(ResultType.NONE, ResultType.NONE));
        put(new ActionType.ActionTypeCombo(ActionType.APPROACH_CORNUCOPIA, ActionType.APPROACH_CORNUCOPIA), new ResultTypeCombo(ResultType.INJURY_MINOR, ResultType.INJURY_MINOR));
        put(new ActionType.ActionTypeCombo(ActionType.APPROACH_CORNUCOPIA, ActionType.BARE_ATTACK), new ResultTypeCombo(ResultType.INJURY_MINOR, ResultType.NONE));
        put(new ActionType.ActionTypeCombo(ActionType.APPROACH_CORNUCOPIA, ActionType.RUN), new ResultTypeCombo(ResultType.INJURY_MINOR, ResultType.NONE));
        put(new ActionType.ActionTypeCombo(ActionType.BARE_ATTACK, ActionType.NONE), new ResultTypeCombo(ResultType.NONE, ResultType.INJURY_SEVERE));
        put(new ActionType.ActionTypeCombo(ActionType.BARE_ATTACK, ActionType.APPROACH_CORNUCOPIA), new ResultTypeCombo(ResultType.NONE, ResultType.INJURY_MINOR));
        put(new ActionType.ActionTypeCombo(ActionType.BARE_ATTACK, ActionType.BARE_ATTACK), new ResultTypeCombo(ResultType.INJURY_MINOR, ResultType.INJURY_MINOR));
        put(new ActionType.ActionTypeCombo(ActionType.BARE_ATTACK, ActionType.RUN), new ResultTypeCombo(ResultType.NONE, ResultType.NONE));
        put(new ActionType.ActionTypeCombo(ActionType.RUN, ActionType.NONE), new ResultTypeCombo(ResultType.NONE, ResultType.NONE));
        put(new ActionType.ActionTypeCombo(ActionType.RUN, ActionType.APPROACH_CORNUCOPIA), new ResultTypeCombo(ResultType.NONE, ResultType.INJURY_MINOR));
        put(new ActionType.ActionTypeCombo(ActionType.RUN, ActionType.BARE_ATTACK), new ResultTypeCombo(ResultType.NONE, ResultType.NONE));
        put(new ActionType.ActionTypeCombo(ActionType.RUN, ActionType.RUN), new ResultTypeCombo(ResultType.NONE, ResultType.NONE));
    }};


    public HungerGamesAction(HungerGamesTribute tribute, ActionType actionType, HungerGamesGame.Location location) {
        this.tribute = tribute;
        this.actionType = actionType;
        this.location = location;
    }

    public enum ActionType {
        NONE(0),
        APPROACH_CORNUCOPIA(1),
        BARE_ATTACK(2),
        RUN(3),
        ;

        private final int id;

        ActionType(int id) {
            this.id = id;
        }

        public static class ActionTypeCombo {

            @Getter
            private final ActionType a;
            @Getter
            private final ActionType b;

            @Override
            public int hashCode() {
                return a.id * 10000 + b.id;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof ActionTypeCombo && hashCode() == obj.hashCode();
            }

            public ActionTypeCombo(ActionType a, ActionType b) {
                this.a = a;
                this.b = b;
            }

        }
    }

    public static Set<HungerGamesActionResult> compute(Set<HungerGamesAction> actions) {
        System.out.println(actions.iterator().next().location);
        System.out.println(actions.iterator().next().actionType);
        System.out.println(actions.iterator().next().tribute.getId());

        Map<HungerGamesAction, ResultType> resultsRaw = new HashMap<>();

        for (HungerGamesGame.Location location : getAllActionLocations(actions)) {
            System.out.println("Running location " + location.name());
            List<HungerGamesAction> actionsAtLocation = actions.stream().filter(a -> a.getLocation().equals(location)).collect(Collectors.toList());
            HungerGamesAction current = actionsAtLocation.get(0);
            actionsAtLocation.remove(0);
            for (HungerGamesAction next : actionsAtLocation) {
                System.out.println("Running action tID " + next.getTribute().getId());
                ActionType actionA = next.getActionType();
                ActionType actionB = current.getActionType();

                ResultTypeCombo resultTypeCombo = actionDictionary.get(new ActionType.ActionTypeCombo(actionA, actionB));

                System.out.println(actionDictionary.keySet().stream().findFirst().get().hashCode());
                System.out.println(new ActionType.ActionTypeCombo(actionA, actionB).hashCode());
                System.out.println(resultTypeCombo == null);

                resultsRaw.put(next, resultTypeCombo.getA());
                resultsRaw.put(current, resultTypeCombo.getB());

                current = next;
            }
        }

        for (Map.Entry<HungerGamesAction, ResultType> entry : resultsRaw.entrySet()) {
            System.out.println(entry.getKey().actionType.name());
            System.out.println(entry.getValue().name());
            System.out.println("\n");
        }

        return new HashSet<HungerGamesActionResult>() {{
            //
        }};
    }

    private static Set<HungerGamesGame.Location> getAllActionLocations(Set<HungerGamesAction> actions) {
        Set<HungerGamesGame.Location> locations = new HashSet<>();
        for (HungerGamesAction action : actions)
            if (!locations.contains(action.getLocation()))
                locations.add(action.getLocation());
        return locations;
    }

}
