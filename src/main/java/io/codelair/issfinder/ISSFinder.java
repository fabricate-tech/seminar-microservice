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

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * This marker-class shows that we want to expose our {@code JAX-RS}-services on the root context path {@code /}.
 *
 * @author <a href="mailto:daniel@redbridge.se">Daniel Pfeifer</a>
 */
@ApplicationPath("/")
public class ISSFinder extends Application {
  // intentionally left empty
}
