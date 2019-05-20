package com.cityradical.pictura.Pipeline;

public class PipelineRunner<T, R> implements Runnable {

    private long numInputQueuePoisonPillsSeen;

    private final Pipeline<R, T> pipeline;

    public PipelineRunner(Pipeline<R, T> pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void run() {
        pipeline.initialize();
        numInputQueuePoisonPillsSeen = 0;
        for (;;) {
            var input = pipeline.getFromInput();
            if (input.equals(pipeline.getInputPoisonPill())) {
                numInputQueuePoisonPillsSeen++;
                if (numInputQueuePoisonPillsSeen >= pipeline.getNumInputProducers())
                {
                    for (var i = 0; i < pipeline.getNumOutputConsumers(); i++) {
                        pipeline.addToOutput(pipeline.getOutputPoisonPill());
                    }
                    pipeline.cleanup();
                    return;
                }
            } else {
                var output = pipeline.process(input);
                pipeline.addToOutput(output);
            }
        }
    }
}
