package com.bluecc.meta.common;

import com.beust.jcommander.internal.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
public class EntityMeta {
    String name;
    @Singular
    List<FieldMeta> fields=Lists.newArrayList();
    @Singular
    List<RelationMeta> relations=Lists.newArrayList();
    int pksSize;
    List<String> pks=Lists.newArrayList();
    boolean isView;

    @Data
    @Builder
    public static class FieldMeta{
        String name;
        String type;
        String col;
        boolean pk;
        boolean notNull;
        boolean encrypt;
        boolean autoCreatedInternal;
        List<String> validators= Lists.newArrayList();
    }

    @Data
    @Builder
    public static class RelationMeta{
        String name;
        String type;
        String relEntityName;
        String fkName;
        @Singular
        List<KeymapMeta> keymaps=Lists.newArrayList();
        boolean autoRelation;
    }

    @Data
    @Builder
    public static class KeymapMeta{
        String fieldName;
        String relFieldName;
    }
}
