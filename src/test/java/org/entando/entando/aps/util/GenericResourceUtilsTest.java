package org.entando.entando.aps.util;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GenericResourceUtilsTest {

    @Test
    void testIsResourceLinkableByContent() {
        int d0 = -1, d1 = -1, d2 = -1, d3 = -1;
        int e0 = TD.GROUPS.length, e1 = TD.GROUPS[1].length, e2 = TD.GROUPS[2].length, e3 = TD.GROUPS[3].length;

        try {
            // CONTAINER OWNER GROUP -----------------------------------------------------------------------------------
            for (d0 = 0; d0 < e0; d0++) {
                String contentOwnerGroup = TD.GROUPS[0][d0];

                // CONTAINER OWNER EXTRA GROUP -------------------------------------------------------------------------
                for (d1 = 0; d1 < e1; d1++) {
                    String contentExtraGroups = TD.GROUPS[1][d1];

                    // RESOURCE OWNER GROUP ----------------------------------------------------------------------------
                    for (d2 = 0; d2 < e2; d2++) {
                        String resourceOwnerGroup = TD.GROUPS[3][d2];

                        // RESOURCE EXTRA GROUP ------------------------------------------------------------------------
                        for (d3 = 0; d3 < e3; d3++) {
                            String resourceExtraGroups = TD.GROUPS[3][d3];

                            boolean actualRes = GenericResourceUtils.isResourceLinkableByContent(
                                    resourceOwnerGroup,
                                    asList(resourceExtraGroups),
                                    contentOwnerGroup,
                                    asList(contentExtraGroups)
                            );

                            boolean expectedRes = TD.truthTable_resourceLinkability[d0][d1][d2][d3];

                            Assertions.assertEquals(expectedRes, actualRes);
                        }
                    }
                }
            }
        } catch (java.lang.AssertionError ex) {
            System.err.println("\nAt coordinates:\nCO[\n"
                    + "\t" + TD.GROUPS[0][d0] + "(" + d0 + ")\n"
                    + "\t" + TD.GROUPS[1][d1] + "(" + d1 + ")\n]\nRE[\n"
                    + "\t" + TD.GROUPS[2][d2] + "(" + d2 + ")\n"
                    + "\t" + TD.GROUPS[3][d3] + "(" + d3 + ")\n]\n");
            throw ex;
        }
    }

    private List<String> asList(String resourceExtraGroups) {
        return (resourceExtraGroups == null) ? null : Arrays.asList(resourceExtraGroups.split(","));
    }

    static class TD {

        static final String[][] GROUPS = new String[][]{
                {"free", "administrators", "GROUP1", "GROUP2"},
                {"free", "administrators", "GROUP1", "GROUP2",
                        "free,administrators", "free,administrators,GROUP1", "GROUP1,GROUP2", null},
                {"free", "administrators", "GROUP1", "GROUP2"},
                {"free", "administrators", "GROUP1", "GROUP2", null},
        };

        /**
         * Truth table of Linking Compatibility  of resources with resources
         * <pre>
         * Array dimensions(X,Y,Z,W) interpretation:
         * - Can an object of owner group X
         * - And extra group Y
         * - Link a resource of owner group Z
         * - And extra group W?
         *
         * Check {@link #GROUPS} for the interpretation of the dimensions.
         *
         * Please note that "GROUP1" and "GROUP2" are sample groups used to encode comparisons between tro different normal
         * groups.
         *
         * <p>#DOCREF b6ee967f-d974-4670-b1c2-823236672342</p>
         */

        private static final boolean[][][][] truthTable_resourceLinkability;

        static {
            final boolean T = true;
            final boolean F = false;

            truthTable_resourceLinkability = new boolean[][][][]{
                    {
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                    },
                    {
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, T, T, T, T}, {T, T, T, T, T}, {T, T, T, T, T}},
                            {{T, T, T, T, T}, {T, F, T, F, F}, {T, T, T, T, T}, {T, F, T, F, F}},
                            {{T, T, T, T, T}, {T, F, F, T, F}, {T, F, F, T, F}, {T, T, T, T, T}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, T, F}, {T, F, T, F, F}},
                            {{T, T, T, T, T}, {T, T, T, T, T}, {T, T, T, T, T}, {T, T, T, T, T}},
                    },
                    {
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, T, F, F}, {T, T, T, T, T}, {T, F, T, F, F}},
                            {{T, T, T, T, T}, {T, F, T, F, F}, {T, T, T, T, T}, {T, F, T, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, T, F}, {T, F, T, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, T, F}, {T, F, T, F, F}},
                            {{T, T, T, T, T}, {T, F, T, F, F}, {T, T, T, T, T}, {T, F, T, F, F}},
                    },
                    {
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, T, F}, {T, F, F, T, F}, {T, T, T, T, T}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, T, F}, {T, F, T, F, F}},
                            {{T, T, T, T, T}, {T, F, F, T, F}, {T, F, F, T, F}, {T, T, T, T, T}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, F, F}, {T, F, F, F, F}},
                            {{T, T, T, T, T}, {T, F, F, F, F}, {T, F, F, T, F}, {T, F, T, F, F}},
                            {{T, T, T, T, T}, {T, F, F, T, F}, {T, F, F, T, F}, {T, T, T, T, T}},
                    },
            };
        }
    }

}