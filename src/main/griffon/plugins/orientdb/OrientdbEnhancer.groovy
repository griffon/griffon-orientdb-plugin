/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package griffon.plugins.orientdb

import griffon.util.CallableWithArgs
import com.orientechnologies.orient.core.db.ODatabase
import com.orientechnologies.orient.core.record.impl.ODocument

/**
 * @author Andres Almiray
 */
final class OrientdbEnhancer {
    private OrientdbEnhancer() {}
    
    static void enhance(MetaClass mc, OrientdbProvider provider = OrientdbDatabaseHolder.instance) {
        mc.withOrientdb = {Closure closure ->
            provider.withOrientdb('default', closure)
        }
        mc.withOrientdb << {String databaseName, Closure closure ->
            provider.withOrientdb(databaseName, closure)
        }
        mc.withOrientdb << {CallableWithArgs callable ->
            provider.withOrientdb('default', callable)
        }
        mc.withOrientdb << {String databaseName, CallableWithArgs callable ->
            provider.withOrientdb(databaseName, callable)
        }
    }
    
    static void enhanceOrient() {
        ExpandoMetaClass.enableGlobally()

        ODatabase.metaClass.withTransaction = { Closure closure ->
            delegate.begin()
            closure()
            delegate.commit()
        }
        
        ODocument.metaClass.propertyMissing = { String propertyName ->
            delegate.field(propertyName)
        }
        
        ODocument.metaClass.propertyMissing = { String propertyName, value ->
            delegate.field(propertyName, value)
        }
    }
}
