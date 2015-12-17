package com.github.aesteve.vertx.groovy.promise

import groovy.transform.CompileStatic
import io.vertx.core.Handler

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

// @CompileStatic // doesnt work in Eclipse
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
class PromiseTransformation implements ASTTransformation {

	@Override
	void visit(ASTNode[] nodes, SourceUnit source) {
		if (!nodes || nodes.length == 0) return
		if (!(nodes[0] instanceof ModuleNode)) return
		
		ModuleNode node = nodes[0] as ModuleNode
		List<ClassNode> classes = node.classes
		if (!classes) return
		Map<ClassNode, List<MethodNode>> newMethods = [:]
		classes.each { clazz ->
			println clazz
			List<MethodNode> methods = clazz.methods
			methods.each { MethodNode method ->
				if (!isHandlerMethod(method)) return
				List<MethodNode> news = newMethods[clazz]
				if (!news) {
					news = []
					newMethods[clazz] = news
				}
				news << makeSyncMethod(method)
			}
		}
		newMethods.each { clazz, methods ->
			methods.each {
				clazz.addMethod it
			}
		}
				
	}

	private boolean isHandlerMethod(MethodNode method) {
		Parameter[] params = method.parameters
		params.find {
			it.type.isDerivedFrom(new ClassNode(Handler.class))
		}
	}
	
	private MethodNode makeSyncMethod(MethodNode asyncMethod) {
		Parameter[] params = asyncMethod.parameters
		params = params.take(params.size() - 1)
		List<ASTNode> nodes = new AstBuilder().buildFromSpec {
			method(asyncMethod.name, org.codehaus.groovy.ast.MethodNode.ACC_PUBLIC, String.class) {
				parameters {
					params.each { Parameter param ->
						parameter "${param.name}": param.type.typeClass
					}
				}
				exceptions {}
				block { 
					returnStatement {
						constant "Hello!" 
					}
				}
			}
		}
		nodes[0] as MethodNode
	}
}
