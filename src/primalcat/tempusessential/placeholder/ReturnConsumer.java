package primalcat.tempusessential.placeholder;
@FunctionalInterface
public interface ReturnConsumer<T, G> {

	G accept(T t);
	
}