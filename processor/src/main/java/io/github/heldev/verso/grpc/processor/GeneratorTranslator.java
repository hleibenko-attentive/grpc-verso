package io.github.heldev.verso.grpc.processor;

import io.github.heldev.verso.grpc.interfaces.VersoCustomGenerator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import java.util.Collection;
import java.util.stream.Collectors;

import static io.github.heldev.verso.grpc.processor.common.Utils.panic;
import static javax.lang.model.util.ElementFilter.methodsIn;

public class GeneratorTranslator {

	public GeneratorCatalog loadGenerators(RoundEnvironment roundEnv) {
		Collection<Generator> generators = methodsIn(roundEnv.getElementsAnnotatedWith(VersoCustomGenerator.class)).stream()
				.peek(method -> {if (! method.getParameters().isEmpty()) {
					throw panic("field translators should have no parameters " + method);
				}})
				.map(this::buildGenerator)
				.collect(Collectors.toList());

		return GeneratorCatalog.of(generators);
	}

	private Generator buildGenerator(ExecutableElement method) {
		return Generator.builder()
				.location(method.getEnclosingElement().asType())
				.method(method.getSimpleName().toString())
				.name(method.getAnnotation(VersoCustomGenerator.class).value())
				.build();
	}


}
