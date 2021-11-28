package io.github.heldev.verso.grpc.processor;

import io.github.heldev.verso.grpc.interfaces.VersoClass;
import io.github.heldev.verso.grpc.processor.common.DefinitionCatalog;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetConverterRenderer;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetType;
import io.github.heldev.verso.grpc.processor.prototranslation.VersoClassTranslator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static javax.lang.model.util.ElementFilter.typesIn;

public class Generator {

	private final DefinitionLoader definitionLoader;
	private final TargetConverterRenderer converterRenderer;
	private final VersoClassTranslator classTranslator;

	public Generator(DefinitionLoader definitionLoader, TargetConverterRenderer converterRenderer) {
		this.definitionLoader = definitionLoader;
		this.converterRenderer = converterRenderer;
		this.classTranslator = new VersoClassTranslator();
	}

	public String generate(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		DefinitionCatalog definitionCatalog = definitionLoader.loadDefinitions();
		List<TargetType> targetTypes = typesIn(roundEnv.getElementsAnnotatedWith(VersoClass.class)).stream()
				.map(classTranslator::translate)
				.collect(toList());


		return targetTypes.toString();
	}
}
