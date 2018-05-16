/*
 * Copyright 2018 StreamSets Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.streamsets.pipeline.api.interceptor;

import com.streamsets.pipeline.api.BlobStore;
import com.streamsets.pipeline.api.ConfigIssue;
import com.streamsets.pipeline.api.ErrorCode;
import com.streamsets.pipeline.api.Record;

import java.util.List;
import java.util.Map;

/**
 * Interceptor is a plugable component similar to Stage. However unlike stage, it's not visible to user but rather is
 * run "hidden" inside the execution framework. This allows interceptors perform job that user should not be able to
 * disable.
 */
public interface Interceptor {

  /**
   * Context that provides runtime information and services to the interceptor.
   */
  public interface Context {

    /**
     * Creates a configuration issue for the interceptor (at initialization time).
     *
     * @param errorCode the <code>ErrorCode</code> for the issue.
     * @param args the arguments for the <code>ErrorCode</code> message.
     * @return the configuration issue to report back.
     */
    ConfigIssue createConfigIssue(ErrorCode errorCode, Object... args);

    /**
     * Return value for given configuration option from data collector main configuration.
     *
     * @param configName Configuration option name
     * @return String representation of the value or null if it's not defined.
     */
    public String getConfig(String configName);

    /**
     * Returns SDC singleton instance for the BLOB store
     */
    public BlobStore getBlobStore();
  }

  /**
   * Initializes the interceptor.
   *
   * This method is called once when the pipeline is being initialized before the processing any data.
   *
   * If the method returns true then it is considered ready to process data. Else it is considered mis-configured or
   * that there is a problem and the interceptor is not ready to process data, thus aborting the pipeline initialization.
   *
   * @param parameters the interceptor configuration.
   * @param context the stage context.
   */
  public List<ConfigIssue> init(Map<String, String> parameters, Context context);

  /**
   * Intercept records and perform operations that need to be performed.
   *
   * @param records List of input records
   * @return List of records that should be passed down to the processing.
   */
  public List<Record> intercept(List<Record> records);

  /**
   * Destroys the interceptor. It should be used to release any resources held by the interceptor after initialization or
   * processing.
   *
   * This method is called once when the pipeline is being shutdown. After this method is called, the interceptor will not
   * be called to process any more data.
   *
   * This method is also called after a failed initialization to allow releasing resources created before the
   * initialization failed.
   */
  void destroy();
}
