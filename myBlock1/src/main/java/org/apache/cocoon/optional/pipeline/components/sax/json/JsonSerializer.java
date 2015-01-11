/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cocoon.optional.pipeline.components.sax.json;

import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.JDomReader;
import java.util.Map;
import org.apache.cocoon.pipeline.caching.CacheKey;
import org.apache.cocoon.pipeline.caching.SimpleCacheKey;
import org.apache.cocoon.pipeline.component.CachingPipelineComponent;
import org.apache.cocoon.sax.AbstractSAXSerializer;
import org.jdom.input.SAXHandler;

/**
 * Serialize SAX events into a JSON string.
 */
public class JsonSerializer extends AbstractSAXSerializer implements CachingPipelineComponent {

    private static final String JSON_UTF_8 = "application/json;charset=utf-8";

    private SAXHandler saxHandler;

    @Override
    public String getContentType() {
        return JSON_UTF_8;
    }

    @Override
    public void setup(final Map<String, Object> inputParameters) {
        this.saxHandler = new SAXHandler();
        this.setContentHandler(this.saxHandler);
    }

    @Override
    public void finish() {
        new HierarchicalStreamCopier().copy(
                new JDomReader(this.saxHandler.getDocument()),
                new JettisonMappedXmlDriver().createWriter(this.getOutputStream()));
    }

    @Override
    public CacheKey constructCacheKey() {
        return new SimpleCacheKey();
    }
}
