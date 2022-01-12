package io.github.heldev.verso.grpc.processor.prototranslation;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.github.heldev.verso.grpc.processor.Generator;

import java.util.List;
import java.util.Optional;

import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

public class TargetConverterRenderer {
	public static final String TO_BUILDER_METHOD = "toBuilder";

	public JavaFile render(TargetTranslatorsViewModel translator) {
		return JavaFile.builder(translator.javaPackage(), renderClass(translator))
				.indent("\t")
				.build();
	}

	private TypeSpec renderClass(TargetTranslatorsViewModel translator) {
		return classBuilder(translator.name())
				.addModifiers(PUBLIC, ABSTRACT)
				.addMethod(constructorBuilder().addModifiers(PRIVATE).build())
				.addMethod(renderToInstanceMethod(translator))
				.addMethod(renderToBuilderMethod(translator))
				.build();
	}

	private MethodSpec renderToInstanceMethod(TargetTranslatorsViewModel translator) {
		return methodBuilder("fromMessage")
				.addModifiers(PUBLIC, STATIC)
				.returns(TypeName.get(translator.targetType()))
				.addParameter(TypeName.get(translator.sourceType()), "message")
				.addStatement("return $L(message).build()", TO_BUILDER_METHOD)
				.build();
	}

	private MethodSpec renderToBuilderMethod(TargetTranslatorsViewModel translator) {
		TypeName builderType = TypeName.get(translator.targetBuilderType());

		return methodBuilder(TO_BUILDER_METHOD)
				.addModifiers(PUBLIC, STATIC)
				.returns(builderType)
				.addParameter(TypeName.get(translator.sourceType()), "message")
				//todo `return $T.$L()` when there no other builder calls
				.addStatement("$T builder = $T.$L()",
						builderType,
						TypeName.get(translator.targetType()),
						translator.builderFactoryMethod())
				.addStatement(renderFluentBuilderCalls(translator))
				.addCode(renderOtherBuilderCalls(translator))
				.addStatement("return builder")
				.build();
	}

	private CodeBlock renderFluentBuilderCalls(TargetTranslatorsViewModel translator) {
		List<CodeBlock> regularAttributeCalls = renderRegularAttributes(translator);
		List<CodeBlock> generatedAttributeCalls = renderGeneratedAttributes(translator);

		int fluentCallCount =  regularAttributeCalls.size() + generatedAttributeCalls.size();
		return fluentCallCount == 0
				? CodeBlock.of("")
				: concat(regularAttributeCalls.stream(), generatedAttributeCalls.stream())
						.collect(CodeBlock.joining("\n", "builder", ""));
	}

	private List<CodeBlock> renderGeneratedAttributes(TargetTranslatorsViewModel translator) {
		return translator.generatedAttributes()
				.stream()
				.map(this::renderGeneratedAttribute)
				.collect(toList());
	}

	private CodeBlock renderGeneratedAttribute(GeneratedAttribute attribute) {
		return CodeBlock.of(".$L($L)",
				attribute.name(),
				renderGenerator(attribute.generator()));
	}

	private CodeBlock renderGenerator(Generator generator) {
		return CodeBlock.of("$T.$L()", generator.location(), generator.method());
	}

	private List<CodeBlock> renderRegularAttributes(TargetTranslatorsViewModel translator) {
		return translator.regularAttributes()
				.stream()
				.map(this::renderRegularAttribute)
				.collect(toList());
	}

	private CodeBlock renderRegularAttribute(AttributeViewModel attribute) {
		return CodeBlock.of(
				".$L($L)",
				attribute.builderSetter(),
				renderRegularAttributeArgument(attribute));
	}

	private CodeBlock renderRegularAttributeArgument(AttributeViewModel attribute) {
		return attribute.sourcePresenceCheckingMethod()
				.map(presenceChecker -> renderOptionalArgument(attribute, presenceChecker))
				.orElseGet(() -> renderSetterArgument(attribute));
	}

	private CodeBlock renderOptionalArgument(AttributeViewModel attribute, String presenceChecker) {
		return CodeBlock.builder()
				.add("$T.of($L).filter(ignored -> message.$L())"
						, Optional.class
						, renderSourceGetter(attribute)
						, presenceChecker)
				.add(attribute.translator()
						.map(translator -> CodeBlock.of(".map($T::$L)"
								, TypeName.get(translator.clazz())
								, translator.method()))
						.orElseGet(CodeBlock.builder()::build))
				.build();
	}

	private CodeBlock renderOtherBuilderCalls(TargetTranslatorsViewModel translator) {
		return translator.optionalAttributesWithNonOptionalArguments()
				.stream()
				.map(attribute -> attribute.sourcePresenceCheckingMethod()
						.map(presenceChecker -> CodeBlock.builder()
								.beginControlFlow("if (message.$L())", presenceChecker)
								.addStatement(renderSetterWithArgument(attribute))
								.endControlFlow()
								.build())
						.orElseGet(() -> renderSetterWithArgument(attribute)))
				.collect(CodeBlock.joining("\n"));
	}

	private CodeBlock renderSetterWithArgument(AttributeViewModel attribute) {
		return CodeBlock.of("builder.$L($L)", attribute.builderSetter(), renderSetterArgument(attribute));
	}

	private CodeBlock renderSetterArgument(AttributeViewModel attribute) {
		return CodeBlock.builder()
				.add(attribute.translator()
						.map(translator -> CodeBlock.of("$T.$L($L)"
								, TypeName.get(translator.clazz())
								, translator.method()
								, renderSourceGetter(attribute)))
						.orElseGet(() -> renderSourceGetter(attribute)))
				.build();
	}

	private CodeBlock renderSourceGetter(AttributeViewModel attribute) {
		return CodeBlock.of("message.$L()", attribute.sourceGetter());
	}
}
