package com.georlegacy.general.vestrimu.core.objects.hungergames;

import lombok.Getter;

public class HungerGamesActionResult {

    public enum ResultType {
        DEATH,
        NONE,
        INJURY_MINOR,
        INJURY_SEVERE,
        ;

        public static class ResultTypeCombo {

            @Getter
            private final ResultType a;
            @Getter
            private final ResultType b;

            public ResultTypeCombo(ResultType a, ResultType b) {
                this.a = a;
                this.b = b;
            }

        }
    }

}
