package brave.webmvc;

import static brave.webmvc.ConfigKeys.TRACING_REPORTER_URL;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.CDI;

import brave.Tracing;
import brave.context.log4j2.ThreadContextScopeDecorator;
import brave.propagation.B3Propagation;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.sampler.Sampler;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import zipkin2.Span;
import zipkin2.codec.Encoding;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

@ApplicationScoped
class TracingProducer {

    private static final Logger log = Logger.getLogger(TracingProducer.class.getName());

    /**
     * @return configured brave tracing
     */
    public static Tracing createTracing() {
        return CDI.current().select(Tracing.class).get();
    }

    /**
     * {@link Dependent} scope is used. {@link ApplicationScoped} does not work because
     * {@link brave.Tracing#clock(brave.propagation.TraceContext)} is a {@code final} method.
     *
     * @param spanReporter injected reporter
     * @param enabled value of {@link ConfigKeys#TRACING_ENABLED}
     * @param localServiceName value of {@link ConfigKeys#TRACING_NAME}
     *
     * @return the configured brave tracing
     */
    @Produces
    @Dependent
    Tracing createTracing(final Reporter<Span> spanReporter,
                          @ConfigProperty(name = ConfigKeys.TRACING_ENABLED) boolean enabled,
                          @ConfigProperty(name = ConfigKeys.TRACING_NAME) String localServiceName) {

        final Tracing.Builder builder = Tracing.newBuilder();

        if (!enabled) {
            final Tracing noOpTracing = builder.build();
            noOpTracing.setNoop(true);
            return noOpTracing;
        }

        if (null != localServiceName && localServiceName.length() > 0) {
            log.log(Level.INFO, "Setting localServiceName to: " + localServiceName);
            builder.localServiceName(localServiceName);
        }

        if (null != spanReporter) {
            log.log(Level.INFO, "Setting span reporter to: " + spanReporter);
            builder.spanReporter(spanReporter);
        }

        return builder
                .sampler(Sampler.ALWAYS_SAMPLE)
                .trackOrphans()
                .propagationFactory(B3Propagation.FACTORY)
                .currentTraceContext(
                        ThreadLocalCurrentTraceContext.newBuilder()
                                .addScopeDecorator(ThreadContextScopeDecorator.create())
                                .build())
                .build();
    }

    @Produces
    @Dependent
    Reporter<zipkin2.Span> zipkinReporter(@ConfigProperty(name = TRACING_REPORTER_URL) final String url) {

        if (null == url || url.length() == 0) {
            log.log(Level.INFO, "Zipkin reporting disabled due to missing value for configuration key: "
                    + TRACING_REPORTER_URL);
            return null;
            // Alternative: return Reporter.NOOP;
        }

        log.log(Level.INFO, "Span reporting enabled. URL: " + url);
        return AsyncReporter.create(
                OkHttpSender.newBuilder()
                        .endpoint(url)
                        .encoding(Encoding.JSON)
                        .build());
    }

    /**
     * @param tracing the instance to be closed
     */
    void close(@Disposes final Tracing tracing) {
        log.log(Level.INFO, "Closing: " + tracing);
        tracing.close();
    }
}
