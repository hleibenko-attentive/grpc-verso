package io.github.heldev.verso.grpc.processor;

import io.github.heldev.verso.grpc.interfaces.VersoClass;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static javax.lang.model.util.ElementFilter.typesIn;

public class Generator {

	private final DefinitionLoader definitionLoader;
	private final TargetConverterRenderer converterRenderer;

	public Generator(DefinitionLoader definitionLoader, TargetConverterRenderer converterRenderer) {
		this.definitionLoader = definitionLoader;
		this.converterRenderer = converterRenderer;
	}

	public String generate(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		DefinitionCatalog definitionCatalog = definitionLoader.loadDefinitions();
		List<TargetClass> targetClasses = typesIn(roundEnv.getElementsAnnotatedWith(VersoClass.class)).stream().map(TargetClass::new).collect(toList());

		targetClasses.stream().map(targetClass -> 1);

		return "";
	}
}
