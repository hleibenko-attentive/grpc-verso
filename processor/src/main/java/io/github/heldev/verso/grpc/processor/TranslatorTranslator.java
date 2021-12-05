package io.github.heldev.verso.grpc.processor;

import io.github.heldev.verso.grpc.interfaces.VersoFieldTranslator;
import io.github.heldev.verso.grpc.processor.prototranslation.Translator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;
import java.util.Collection;
import java.util.stream.Collectors;

import static javax.lang.model.util.ElementFilter.methodsIn;

public class TranslatorTranslator {
	private final Elements elementUtils;

	public TranslatorTranslator(Elements elementUtils) {
		this.elementUtils = elementUtils;
	}

	public TranslatorCatalog loadFieldTranslators(RoundEnvironment roundEnv) {
		Collection<Translator> translators = methodsIn(roundEnv.getElementsAnnotatedWith(VersoFieldTranslator.class)).stream()
				.peek(method -> {if (method.getParameters().size() != 1) {
					throw new RuntimeException("field translators should have exactly 1 parameter " + method);
				}})
				.map(this::buildTranslator
				).collect(Collectors.toList());

		return TranslatorCatalog.of(translators);
	}

	private Translator buildTranslator(ExecutableElement method) {
		return Translator.builder()
				//todo check if EnclosingElement is always a class (e.g. lambdas)
				.location(method.getEnclosingElement().asType())
				.method(method.getSimpleName().toString())
				.from(method.getParameters().get(0).asType())
				.to(method.getReturnType())
				.build();
	}
}
