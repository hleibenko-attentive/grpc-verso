package io.github.heldev.verso.grpc.processor;

import io.github.heldev.verso.grpc.interfaces.VersoClass;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.lang.model.util.ElementFilter.typesIn;

public class VersoProcessor extends AbstractProcessor {

	private Generator generator;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		System.out.println("d");
//		throw new RuntimeException("failed i");
//		super.init(processingEnv);
//		System.out.println("\n\n\n\t\t\tINITME\n\n\n");
//		generator = new Generator(new DefinitionLoader(), new TargetConverterRenderer());
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		System.out.println("\n\n\n\t\t\tTESTME\n\n\n");

//		throw new RuntimeException("failed p");

//		System.out.println(generator.generate(annotations, roundEnv));
		return true;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return new HashSet<>();
//		throw new RuntimeException("failed s");

//		return Stream.of(VersoClass.class).map(Class::getCanonicalName).collect(toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}
}
