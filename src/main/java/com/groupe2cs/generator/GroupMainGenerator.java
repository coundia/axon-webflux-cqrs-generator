package com.groupe2cs.generator;

import com.groupe2cs.generator.config.GeneratorProperties;
import com.groupe2cs.generator.dto.ApiResponseDto;
import com.groupe2cs.generator.dto.EntityDefinitionDTO;
import com.groupe2cs.generator.model.EntityDefinition;
import com.groupe2cs.generator.service.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class GroupMainGenerator {

    private final GeneratorProperties properties;
    private final CommandGeneratorService commandGenerator;
    private final AggregateGeneratorService aggregateGenerator;
    private final VoGeneratorService voGenerator;
    private final EventGeneratorService eventGenerator;
    private final ProjectionGeneratorService projectionGenerator;
    private final DtoRequestGeneratorService dtoRequestGeneratorService;
    private final DtoResponseGeneratorService dtoResponseGeneratorService;
    private final MapperGeneratorService mapperGenerator;
    private final ExceptionGeneratorService exceptionGeneratorService;
    private final RepositoryGeneratorService repositoryGeneratorService;
    private final EntityGeneratorService entityGeneratorService;
    private final ListQueryGeneratorService listQueryGeneratorService;
    private final ListQueryHandlerGeneratorService listQueryHandlerGeneratorService;
    private final ListControllerGeneratorService listControllerGeneratorService;
    private final PagedResponseGeneratorService pagedResponseGeneratorService;
    private final CreateControllerGeneratorService createControllerGeneratorService;
    private final ControllerIntegrationTestGeneratorService testControllerIntegrationTestGeneratorService;


    private EntityDefinition loadFromFileDefinition() {
        return EntityDefinition.fromSource(
                properties.getEntityName(),
                properties.getSourceRoot()
        );
    }

    private void log(String message) {
        System.out.println(message);
    }

    public GroupMainGenerator(
            GeneratorProperties properties,
            CommandGeneratorService commandGenerator,
            AggregateGeneratorService aggregateGenerator,
            VoGeneratorService voGenerator,
            EventGeneratorService eventGenerator,
            ProjectionGeneratorService projectionGenerator,
            DtoRequestGeneratorService dtoRequestGeneratorService,
            MapperGeneratorService mapperGenerator,
            DtoResponseGeneratorService dtoResponseGeneratorService,
            ExceptionGeneratorService exceptionGeneratorService,
            RepositoryGeneratorService repositoryGeneratorService,
            EntityGeneratorService entityGeneratorService,
            ListQueryGeneratorService listQueryGeneratorService,
            ListQueryHandlerGeneratorService  listQueryHandlerGeneratorService,
            ListControllerGeneratorService listControllerGeneratorService,
            PagedResponseGeneratorService pagedResponseGeneratorService,
            CreateControllerGeneratorService createControllerGeneratorService,
            ControllerIntegrationTestGeneratorService testControllerIntegrationTestGeneratorService
    ) {
        this.properties = properties;
        this.commandGenerator = commandGenerator;
        this.aggregateGenerator = aggregateGenerator;
        this.voGenerator = voGenerator;
        this.eventGenerator = eventGenerator;
        this.projectionGenerator = projectionGenerator;
        this.dtoRequestGeneratorService = dtoRequestGeneratorService;
        this.mapperGenerator = mapperGenerator;
        this.dtoResponseGeneratorService = dtoResponseGeneratorService;
        this.exceptionGeneratorService = exceptionGeneratorService;
        this.repositoryGeneratorService = repositoryGeneratorService;
        this.entityGeneratorService = entityGeneratorService;
        this.listQueryGeneratorService = listQueryGeneratorService;
        this.listQueryHandlerGeneratorService = listQueryHandlerGeneratorService;
        this.listControllerGeneratorService = listControllerGeneratorService;
        this.pagedResponseGeneratorService = pagedResponseGeneratorService;
        this.createControllerGeneratorService = createControllerGeneratorService;
        this.testControllerIntegrationTestGeneratorService = testControllerIntegrationTestGeneratorService;
    }

    public Flux<ApiResponseDto> generateStreaming(EntityDefinitionDTO definitionDto) {
        Sinks.Many<ApiResponseDto> sink = Sinks.many().unicast().onBackpressureBuffer();

        EntityDefinition definition = definitionDto.getDefinition();
        String outputDir = definitionDto.getOutputDir();

        Mono.fromRunnable(() -> {
            try {
                emit(sink, "Generating Value Objects...");
                voGenerator.generate(definition, outputDir);

                emit(sink, "Generating Events...");
                eventGenerator.generate(definition, outputDir);

                emit(sink, "Generating Aggregate...");
                aggregateGenerator.generate(definition, outputDir);

                emit(sink, "Generating Exception...");
                exceptionGeneratorService.generate(definition, outputDir);

                emit(sink, "Generating Commands...");
                commandGenerator.generate(definition, outputDir);

                emit(sink, "Generating Queries...");
                listQueryGeneratorService.generate(definition, outputDir);

                emit(sink, "Generating Query Handlers...");
                listQueryHandlerGeneratorService.generate(definition, outputDir);

                emit(sink, "Generating DTO Requests...");
                dtoRequestGeneratorService.generate(definition, outputDir);

                emit(sink, "Generating DTO Responses...");
                dtoResponseGeneratorService.generate(definition, outputDir);
                pagedResponseGeneratorService.generate(definition, outputDir);

                emit(sink, "Generating Mappers...");
                mapperGenerator.generate(definition, outputDir);

                emit(sink, "Generating Projections...");
                projectionGenerator.generate(definition, outputDir);

                emit(sink, "Generating Controllers...");
                listControllerGeneratorService.generate(definition, outputDir);
                createControllerGeneratorService.generate(definition, outputDir);

                emit(sink, "Generating Repositories...");
                repositoryGeneratorService.generate(definition, outputDir);

                emit(sink,"Generating entity...");
                entityGeneratorService.generate(definition,outputDir);

                emit(sink,"Generating tests...");
                testControllerIntegrationTestGeneratorService.generate(outputDir);

                emit(sink, "✅ Code generation complete!");

                sink.tryEmitComplete();

            } catch (Exception e) {
                sink.tryEmitNext(ApiResponseDto.builder()
                        .code(500)
                        .message("❌ Error during generation: " + e.getMessage())
                        .build());
                sink.tryEmitComplete();
            }
        }).subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic()).subscribe();

        return sink.asFlux();
    }


    private void emit(Sinks.Many<ApiResponseDto> sink, String msg) {
        sink.tryEmitNext(ApiResponseDto.builder()
                .code(200)
                .message(msg)
                .build());
    }


}
