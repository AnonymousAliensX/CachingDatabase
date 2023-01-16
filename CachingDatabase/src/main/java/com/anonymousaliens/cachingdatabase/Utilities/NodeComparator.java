package com.anonymousaliens.cachingdatabase.Utilities;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class NodeComparator implements Comparator<JsonNode> {

    @Override
    public int compare(JsonNode jsonNode, JsonNode t1) {
        System.out.println(jsonNode);
        System.out.println(t1);
        return 0;
    }

    @Override
    public Comparator<JsonNode> reversed() {
        System.out.println("Then Comparing");
        return Comparator.super.reversed();
    }

    @Override
    public Comparator<JsonNode> thenComparing(Comparator<? super JsonNode> other) {
        System.out.println("Then Comparing");
        return Comparator.super.thenComparing(other);
    }

    @Override
    public <U> Comparator<JsonNode> thenComparing(Function<? super JsonNode, ? extends U> keyExtractor, Comparator<? super U> keyComparator) {
        System.out.println("Then Comparing");
        return Comparator.super.thenComparing(keyExtractor, keyComparator);
    }

    @Override
    public <U extends Comparable<? super U>> Comparator<JsonNode> thenComparing(Function<? super JsonNode, ? extends U> keyExtractor) {
        System.out.println("Then Comparing");
        return Comparator.super.thenComparing(keyExtractor);
    }

    @Override
    public Comparator<JsonNode> thenComparingInt(ToIntFunction<? super JsonNode> keyExtractor) {
        return Comparator.super.thenComparingInt(keyExtractor);
    }

    @Override
    public Comparator<JsonNode> thenComparingLong(ToLongFunction<? super JsonNode> keyExtractor) {
        return Comparator.super.thenComparingLong(keyExtractor);
    }

    @Override
    public Comparator<JsonNode> thenComparingDouble(ToDoubleFunction<? super JsonNode> keyExtractor) {
        return Comparator.super.thenComparingDouble(keyExtractor);
    }
}
