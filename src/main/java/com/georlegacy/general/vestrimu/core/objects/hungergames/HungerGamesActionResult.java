package com.georlegacy.general.vestrimu.core.objects.hungergames;

import lombok.Getter;

import java.util.function.Consumer;

public class HungerGamesActionResult {

    @Getter
    private final HungerGamesTribute tribute;
    @Getter
    private final ResultType type;
    @Getter
    private final String fDat;

    public HungerGamesActionResult(HungerGamesTribute tribute, ResultType type, String fDat) {
        this.tribute = tribute;
        this.type = type;
        this.fDat = fDat;
    }

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
