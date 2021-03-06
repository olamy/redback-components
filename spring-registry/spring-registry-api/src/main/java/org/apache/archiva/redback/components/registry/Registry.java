package org.apache.archiva.redback.components.registry;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * The Plexus registry is a single source of external configuration for Plexus components and applications.
 * It can be used by components to source configuration, knowing that it can be used from within applications
 * without the information being hard coded into the component.
 */
public interface Registry
{

    /**
     * Dump the entire registry to a string, for debugging purposes.
     *
     * @return the registry contents
     */
    String dump();

    /**
     * Get a string value from the registry. If not found, <code>null</code> is returned.
     *
     * @param key the key in the registry
     * @return the value
     */
    String getString( String key );

    /**
     * Get a string value from the registry. If not found, the default value is used.
     *
     * @param key          the key in the registry
     * @param defaultValue the default value
     * @return the value
     */
    String getString( String key, String defaultValue );

    /**
     * Set a string value in the registry.
     *
     * @param key   the key in the registry
     * @param value the value to set
     */
    void setString( String key, String value );

    /**
     * Get an integer value from the registry. If not found, an exception is thrown.
     *
     * @param key the key in the registry
     * @return the value
     * @throws java.util.NoSuchElementException
     *          if the key is not found
     */
    int getInt( String key );

    /**
     * Get an integer value from the registry. If not found, the default value is used.
     *
     * @param key          the key in the registry
     * @param defaultValue the default value
     * @return the value
     */
    int getInt( String key, int defaultValue );

    /**
     * Set an integer value in the registry.
     *
     * @param key   the key in the registry
     * @param value the value to set
     */
    void setInt( String key, int value );

    /**
     * Get a boolean value from the registry. If not found, an exception is thrown.
     *
     * @param key the key in the registry
     * @return the value
     * @throws java.util.NoSuchElementException
     *          if the key is not found
     */
    boolean getBoolean( String key );

    /**
     * Get a boolean value from the registry. If not found, the default value is used.
     *
     * @param key          the key in the registry
     * @param defaultValue the default value
     * @return the value
     */
    boolean getBoolean( String key, boolean defaultValue );

    /**
     * Set a boolean value in the registry.
     *
     * @param key   the key in the registry
     * @param value the value to set
     */
    void setBoolean( String key, boolean value );

    /**
     * Load configuration from the given classloader resource.
     *
     * @param resource the location to load the configuration from
     * @throws RegistryException if a problem occurred reading the resource to add to the registry
     */
    void addConfigurationFromResource( String resource )
        throws RegistryException;

    /**
     * Load configuration from the given classloader resource.
     *
     * @param resource the location to load the configuration from
     * @param prefix   the location to add the configuration at in the registry
     * @throws RegistryException if a problem occurred reading the resource to add to the registry
     */
    void addConfigurationFromResource( String resource, String prefix )
        throws RegistryException;

    /**
     * Load configuration from the given file.
     *
     * @param file the location to load the configuration from
     * @throws RegistryException if a problem occurred reading the resource to add to the registry
     */
    void addConfigurationFromFile( File file )
        throws RegistryException;

    /**
     * Load configuration from the given file.
     *
     * @param file   the location to load the configuration from
     * @param prefix the location to add the configuration at in the registry
     * @throws RegistryException if a problem occurred reading the resource to add to the registry
     */
    void addConfigurationFromFile( File file, String prefix )
        throws RegistryException;

    /**
     * Determine if the registry contains any elements.
     *
     * @return whether the registry contains any elements
     */
    boolean isEmpty();

    /**
     * Get a list of strings at the given key in the registry.
     *
     * @param key the key to lookup
     * @return the list of strings
     */
    List getList( String key );

    /**
     * TODO move to Map which is more generic ?
     * Get the properties at the given key in the registry.
     *
     * @param key the key to lookup
     * @return the properties
     */
    Properties getProperties( String key );

    /**
     * Get a subset of the registry, for all keys descended from the given key.
     *
     * @param key the key to take the subset from
     * @return the registry subset
     */
    Registry getSubset( String key );

    /**
     * Get a list of subsets of the registry, for all keys descended from the given key.
     *
     * @param key the key to take the subsets from
     * @return the registry subsets
     */
    List getSubsetList( String key );

    /**
     * Get a subsection of the registry, identified by the given name. If it doesn't exist, <code>null</code> will be
     * returned.
     *
     * @param name registry section name
     * @return the registry
     */
    Registry getSection( String name );

    /**
     * Save any changes to the registry since it was loaded.
     *
     * @throws RegistryException             if there was a problem saving the registry
     * @throws UnsupportedOperationException if the registry is not writable
     */
    void save()
        throws RegistryException, UnsupportedOperationException;

    /**
     * Add a change listener. Note that settings this on the base registry will only detect 'invalidation' events, not
     * individual changes. You should retrieve the named sub-registry to listen for changes.
     *
     * @param listener the listener
     * @todo this isn't ideal, so maybe fix combined configuration to re-fire it's events to it's own listeners in the c-c implementation
     */
    void addChangeListener( RegistryListener listener );

    /**
     * @param listener
     * @return <code>true</code> if has been removed
     * @since 2.3
     */
    boolean removeChangeListener( RegistryListener listener );

    /**
     * Get all the keys in this registry. Keys are only retrieved at a depth of 1.
     *
     * @return the set of keys
     */
    Collection<String> getKeys();

    /**
     * Get all the keys in this registry.
     * @since 2.1
     * @return the set of keys
     */
    Collection<String> getFullKeys();

    /**
     * Remove a keyed element from the registry.
     *
     * @param key the key to remove
     */
    void remove( String key );

    /**
     * Remove a keyed subset of the registry.
     *
     * @param key the subset to remove
     */
    void removeSubset( String key );

    void initialize() throws RegistryException;
}
