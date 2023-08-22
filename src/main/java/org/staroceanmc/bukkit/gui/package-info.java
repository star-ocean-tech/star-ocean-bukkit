/**
 * Gui Management.
 * <p>
 * Design objectives:
 * <p>
 * {@link org.staroceanmc.bukkit.gui.GuiManager} manages displaying of Gui and their lifecycle state. <br/>
 * {@link org.staroceanmc.bukkit.gui.Gui} actual Gui itself, manages contents of inventory, process player input.
 * Support multiple viewers. <br/>
 * {@link org.staroceanmc.bukkit.gui.GuiActionListener} receives inventory related events.
 * Will pass player input (e.g. click) to Gui and lifecycle events (e.g. close) to GuiManager.
 * </p>
 * </p>
 */
package org.staroceanmc.bukkit.gui;

// TODO: Where should viewers be managed?
