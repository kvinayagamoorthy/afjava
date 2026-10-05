package io.github.kvinayagamoorthy.afjava.labs.javaclint;

import java.util.Arrays;

/**
 * Legitimate, narrow suppression. Java cannot create a generic array, so a cast from Object[] is
 * unavoidable. Suppress on the single local variable, never on the method or class, and say why.
 */
public final class LintSuppression<T> {

  private final T[] items;

  /** Creates a fixed-capacity holder. */
  public LintSuppression(int capacity) {
    // Safe: the array never escapes as T[] to callers; only single elements are returned.
    @SuppressWarnings("unchecked")
    T[] created = (T[]) new Object[capacity];
    this.items = created;
  }

  /** Returns the element at {@code index}. */
  public T get(int index) {
    return items[index];
  }

  /** Stores {@code item} at {@code index}. */
  public void set(int index, T item) {
    items[index] = item;
  }

  @Override
  public String toString() {
    return Arrays.toString(items);
  }
}
