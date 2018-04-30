package io.codelair.issfinder;

public class BrokenEvent {
  private final boolean broken;

  public BrokenEvent(final boolean broken) {
    this.broken = broken;
  }

  public boolean isBroken() {
    return broken;
  }
}
