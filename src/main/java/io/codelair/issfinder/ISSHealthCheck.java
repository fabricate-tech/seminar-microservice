package io.codelair.issfinder;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public class ISSHealthCheck implements HealthCheck {
  private boolean reportBroken;

  public void onBrokenEvent(@Observes final BrokenEvent event) {
    reportBroken = event.isBroken();
  }

  @Override
  public HealthCheckResponse call() {
    return HealthCheckResponse.named("broke").state(!reportBroken).build();
  }
}
