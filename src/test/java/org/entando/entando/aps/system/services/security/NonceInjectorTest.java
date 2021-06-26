package org.entando.entando.aps.system.services.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NonceInjectorTest {

    @Test
    void shouldProcessHtmlAndInsertNonce() {
        String source = "<script    \t key=\"value\"   \tanother_key=\"value\">";
        String expected = "<#assign wp=JspTaglibs[ \"/aps-core\"]>\n<script nonce=\"<@wp.cspNonce />\" key=\"value\" another_key=\"value\">";

        assertThat(NonceInjector.process(source)).isEqualTo(expected);
    }

    @Test
    void shouldNotChangeAlreadyProcessedHtml() {
        String source = "<#assign wp=JspTaglibs[ \"/aps-core\"]>\n<script   \tnonce=\"<@wp.cspNonce />\"     \tkey=value another_key=\"value\">";

        assertThat(NonceInjector.process(source)).isEqualTo(source);
    }

    @Test
    void shouldNotInjectApsCoreLibIfDoesntHaveNonce() {
        String source = "<h1>My Title<h1>";

        assertThat(NonceInjector.process(source)).isEqualTo(source);
    }

    @Test
    void shouldProcessComplexHtmlAndInsertNonceWhereAppropriate() {
        String source = "<html><body>\n"
                + "<script nonce=\"what\" key=\"value\" another_key=\"value\">\n"
                + "<script   key=\"value\"  \t   another_key=\"value\">\n"
                + "<script nonce=\"what\">\n"
                + "<script key=\"value\" >\n"
                + "<script >\n"
                + "<script>\n"
                + "</body></html>";

        String expected = "<#assign wp=JspTaglibs[ \"/aps-core\"]>\n"
                + "<html><body>\n"
                + "<script nonce=\"what\" key=\"value\" another_key=\"value\">\n"
                + "<script nonce=\"<@wp.cspNonce />\" key=\"value\" another_key=\"value\">\n"
                + "<script nonce=\"what\">\n"
                + "<script nonce=\"<@wp.cspNonce />\" key=\"value\" >\n"
                + "<script nonce=\"<@wp.cspNonce />\" >\n"
                + "<script nonce=\"<@wp.cspNonce />\">\n"
                + "</body></html>";

        assertThat(NonceInjector.process(source)).isEqualTo(expected);
    }

}
