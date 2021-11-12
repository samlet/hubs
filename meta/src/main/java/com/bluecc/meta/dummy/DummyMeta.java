package com.bluecc.meta.dummy;

import com.beust.jcommander.internal.Lists;
import com.bluecc.meta.common.EntityMeta;
import com.bluecc.meta.common.EntityMeta.*;
import com.bluecc.meta.common.ServiceMeta;
import com.bluecc.meta.common.ServiceMeta.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

import static com.bluecc.meta.common.ServiceMeta.ServiceMode.*;
import static com.google.common.collect.Lists.newArrayList;

public class DummyMeta {
    public static final Gson GSON = new GsonBuilder()
            // .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
//            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
//             .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
            .setPrettyPrinting()
            .create();
    public static final EntityMeta partyMeta = EntityMeta.builder()
            .name("Party")
            .field(FieldMeta.builder()
                    .name("partyId").type("id").build())
            .field(FieldMeta.builder()
                    .name("lastName").type("string").build())
            .build();

    public static final ServiceMeta createPersonMeta = ServiceMeta.builder()
            .name("createPerson")
            .input(ParamMeta.builder()
                    .name("firstName").type("string")
                    .build())
            .input(ParamMeta.builder()
                    .name("lastName").type("string")
                    .build())
            .eca("commit", newArrayList(
                    Eca.builder()
                            .condition(EcaCondition.builder()
                                    .shortDisplayDescription("[]")
                                    .build())
                            .action(EcaAction.builder()
                                    .serviceName("createParty")
                                    .serviceMode(SYNC)
                                    .build())
                            .build()
            ))
            .build();

    public static void main(String[] args) {
        System.out.println(GSON.toJson(partyMeta));
        System.out.println(GSON.toJson(createPersonMeta));
    }
}
