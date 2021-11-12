package com.bluecc.meta.common;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ServiceMeta {
    String name;
    String defaultEnt;
    String description;

    @Singular
    Map<String, List<Eca>> ecas= Maps.newHashMap();
    @Singular("input")
    List<ParamMeta> paramsInput= Lists.newArrayList();
    @Singular("output")
    List<ParamMeta> paramsOutput=Lists.newArrayList();

    public enum ServiceParamMode {
        IN, OUT, INOUT
    }
    @Data
    @Builder
    public static class ParamMeta{
        String name;
        String type;
        boolean required;
        boolean overrideOptional;
        String entity;
        ServiceParamMode mode;
        boolean internal;
    }

    @Data
    @Builder
    public static class Eca{
        @Singular("condition")
        List<EcaCondition> conditionList;
        @Singular("action")
        List<EcaAction> actionList;
    }
    @Data
    @Builder
    public static class EcaCondition{
        String shortDisplayDescription;
    }

    public enum ServiceMode{
        ASYNC, SYNC
    }
    @Data
    @Builder
    public static class EcaAction{
        String serviceName;
        ServiceMode serviceMode;
    }
}

