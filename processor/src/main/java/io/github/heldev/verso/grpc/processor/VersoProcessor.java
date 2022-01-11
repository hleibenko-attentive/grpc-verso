package io.github.heldev.verso.grpc.processor;

import com.squareup.javapoet.JavaFile;
import io.github.heldev.verso.grpc.interfaces.VersoFieldTranslator;
import io.github.heldev.verso.grpc.interfaces.VersoMessage;
import io.github.heldev.verso.grpc.processor.common.DefinitionAdapter;
import io.github.heldev.verso.grpc.processor.common.DescriptorSetFilepathSource;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetConverterRenderer;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetTranslatorsTranslator;
import io.github.heldev.verso.grpc.processor.prototranslation.TargetTypeTranslator;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static javax.lang.model.util.ElementFilter.typesIn;
import static javax.tools.Diagnostic.Kind.ERROR;

public class VersoProcessor extends AbstractProcessor {
	private TargetConverterRenderer targetConverterRenderer;
	private TranslatorTranslator translatorTranslator;
	private TargetTranslatorsTranslator targetTranslatorsTranslator;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		translatorTranslator = new TranslatorTranslator();
		targetConverterRenderer = new TargetConverterRenderer();

		DefinitionAdapter definitionAdapter = new DefinitionAdapter(
				new DescriptorSetFilepathSource(Paths.get(getDescriptorSetPath(processingEnv))));

		TargetTypeTranslator targetTypeTranslator = new TargetTypeTranslator(
				processingEnv.getTypeUtils(),
				processingEnv.getElementUtils(),
				//todo lazy supplier, parsing doesn't seem to be the best activity for DI context initialization
				definitionAdapter.get());

		targetTranslatorsTranslator = new TargetTranslatorsTranslator(
				targetTypeTranslator,
				processingEnv.getTypeUtils(),
				processingEnv.getElementUtils());
	}

	private String getDescriptorSetPath(ProcessingEnvironment processingEnv) {
		return processingEnv.getOptions()
				.getOrDefault("verso.descriptorSetPath", "/tmp/grpc-verso/descriptorset.protobin");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		generateTranslators(roundEnv, translatorTranslator.loadFieldTranslators(roundEnv));
		return true;
	}

	private void generateTranslators(RoundEnvironment roundEnv, TranslatorCatalog translatorCatalog) {
		typesIn(roundEnv.getElementsAnnotatedWith(VersoMessage.class))
				.forEach(type -> {
					JavaFile file = targetConverterRenderer.render(targetTranslatorsTranslator.translate(translatorCatalog, type));
					try {
						file.writeTo(processingEnv.getFiler());
					} catch (IOException e) {
						StringWriter stringWriter = new StringWriter();
						e.printStackTrace(new PrintWriter(stringWriter));
						processingEnv.getMessager().printMessage(ERROR, stringWriter.toString());
					}
				});
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return Stream.of(VersoFieldTranslator.class, VersoMessage.class)
				.map(Class::getCanonicalName)
				.collect(toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}
}
