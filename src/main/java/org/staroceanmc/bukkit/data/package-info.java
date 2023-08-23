/**
 * Player data storage
 * <br/>
 * <p>
 *     Structure:
 * </p>
 * <p>
 * {@link org.staroceanmc.bukkit.data.PlayerDataRootStorageFactory} Root storage factory, used to create root storages.
 * {@link org.staroceanmc.bukkit.data.PlayerDataRootStorage} Root storage, manages data for all players, manage storages.
 * {@link org.staroceanmc.bukkit.data.PlayerDataStorage} Storage, one instance per player, manages containers.
 * {@link org.staroceanmc.bukkit.data.PlayerDataContainer} Container, one instance per module.
 * </p>
 * Multi-thread stability is not confirmed.
 */
package org.staroceanmc.bukkit.data;