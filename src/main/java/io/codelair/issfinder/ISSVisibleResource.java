/*
 * issfinder by Daniel Pfeifer (RedBridge Group).
 *
 * To the extent possible under law, the person who associated CC0 with
 * issfinder has waived all copyright and related or neighboring rights
 * to issfinder.
 *
 * You should have received a copy of the CC0 legalcode along with this
 * work.  If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package io.codelair.issfinder;

import io.codelair.issfinder.model.Daylight;
import io.codelair.issfinder.remote.OpenNotifyProxy;
import io.codelair.issfinder.remote.SunriseSunsetProxy;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.metrics.annotation.Counted;

@RequestScoped
@Path("/issvisible")
public class ISSVisibleResource {

  @Inject
  private Config config;
  @Inject
  private OpenNotifyProxy openNotifyProxy;
  @Inject
  private SunriseSunsetProxy sunriseSunsetProxy;
  @Inject
  private Event<BrokenEvent> brokenEvent;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Counted(name = "number_of_iss_viewers", absolute = true)
  public Response doGetJson(@QueryParam("lat") Double lat, @QueryParam("lon") Double lon) {
    if (lat == null || lon == null) {
      lat = config.getLatitude();
      lon = config.getLongitude();
    }

    return getIssPassTime(lat, lon)
        .map(dateTime -> Response.ok(Json.createObjectBuilder()
            .add("issVisible", dateTime.toEpochSecond())
            .add("status", "OK")
            .build()))
        .orElse(Response.serverError().entity(Json.createObjectBuilder()
            .addNull("issVisible")
            .add("status", "Failed")
            .build()))
        .build();
  }

  @GET
  @Path("/make_it_fail")
  public Response makeItFail() {
    brokenEvent.fire(new BrokenEvent(true));

    return Response.ok().build();
  }

  @GET
  @Path("/burn_cpu")
  public Response burnCpu() {
    try {
      final KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
      rsa.initialize(2048);
      System.out.println("keyPair = " + rsa.generateKeyPair());
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalArgumentException(e);
    }

    return Response.ok().build();
  }

  private Optional<ZonedDateTime> getIssPassTime(final double lat, final double lon) {
    final Set<Instant> instants = openNotifyProxy.fetchIssPassDates(lat, lon);

    for (final Instant instant : instants) {
      final ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
      final Daylight daylightForDate = sunriseSunsetProxy.fetchDaylightForDate(config.getLatitude(), config.getLongitude(), dateTime);

      if (daylightForDate.isNight(dateTime)) {
        return Optional.of(dateTime);
      }
    }

    return Optional.empty();
  }
}
