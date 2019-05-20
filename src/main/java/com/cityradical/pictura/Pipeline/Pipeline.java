package com.cityradical.pictura.Pipeline;

public interface Pipeline<T, R> {

    default void initialize() { };

    default void cleanup() { };

    R process(T input);

    T getFromInput();

    void addToOutput(R output);

    T getInputPoisonPill();

    long getNumInputProducers();

    R getOutputPoisonPill();

    long getNumOutputConsumers();
}
