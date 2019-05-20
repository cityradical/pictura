package com.cityradical.pictura.Pipeline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.String.format;

public class LoggingPipeline<T, R> implements Pipeline<T, R> {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Pipeline<T, R> pipeline;
    private final String pipelineSimpleName;

    public LoggingPipeline(Pipeline<T, R> pipeline) {
        this.pipeline = pipeline;
        this.pipelineSimpleName = pipeline.getClass().getSimpleName();
    }

    @Override
    public void initialize() {
        LOGGER.info(format("%s initializing", pipelineSimpleName));
        pipeline.initialize();
    }

    @Override
    public void cleanup() {
        LOGGER.info(format("%s cleaning up", pipelineSimpleName));
        pipeline.cleanup();
    }

    @Override
    public R process(T input) {
        LOGGER.info(format("%s started processing", pipelineSimpleName));
        var output = pipeline.process(input);
        LOGGER.info(format("%s finished processing", pipelineSimpleName));
        return output;
    }

    @Override
    public T getFromInput() {
        var input = pipeline.getFromInput();
        LOGGER.info(() -> {
            if (input.equals(getInputPoisonPill())) {
                return format("%s got poison pill from input", pipelineSimpleName);
            } else {
                return format("%s getting an item from input", pipelineSimpleName);
            }
        });
        return input;
    }

    @Override
    public void addToOutput(R output) {
        LOGGER.info(() -> {
            if (output.equals(getOutputPoisonPill())) {
                return format("%s adding poison pill to output", pipelineSimpleName);
            } else {
                return format("%s adding an item to output", pipelineSimpleName);
            }
        });
        pipeline.addToOutput(output);
    }

    @Override
    public T getInputPoisonPill() {
        return pipeline.getInputPoisonPill();
    }

    @Override
    public long getNumInputProducers() {
        return pipeline.getNumInputProducers();
    }

    @Override
    public R getOutputPoisonPill() {
        return pipeline.getOutputPoisonPill();
    }

    @Override
    public long getNumOutputConsumers() {
        return pipeline.getNumOutputConsumers();
    }
}
