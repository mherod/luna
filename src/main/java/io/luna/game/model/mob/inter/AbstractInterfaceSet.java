package io.luna.game.model.mob.inter;

import io.luna.game.model.mob.Player;
import io.luna.net.msg.out.CloseWindowsMessageWriter;
import io.luna.net.msg.out.WalkableInterfaceMessageWriter;

import java.util.Optional;

/**
 * A collection of {@link AbstractInterface}s that are displayed on the Player's game screen.
 *
 * @author lare96 <http://github.org/lare96>
 */
public final class AbstractInterfaceSet {

    /**
     * The player instance.
     */
    private final Player player;

    /**
     * The current standard interface.
     */
    private Optional<StandardInterface> currentStandard = Optional.empty();

    /**
     * The current input interface.
     */
    private Optional<InputInterface> currentInput = Optional.empty();

    /**
     * The current walkable interface.
     */
    private Optional<WalkableInterface> currentWalkable = Optional.empty();

    /**
     * Creates a new {@link AbstractInterfaceSet}.
     *
     * @param player The player instance.
     */
    public AbstractInterfaceSet(Player player) {
        this.player = player;
    }

    /**
     * Opens a new interface.
     *
     * @param inter The interface to open.
     */
    public void open(AbstractInterface inter) {
        if (inter.isStandard()) {
            setCurrentStandard((StandardInterface) inter);
        } else if (inter.isInput()) {
            setCurrentInput((InputInterface) inter);
        } else if (inter.isWalkable()) {
            setCurrentWalkable((WalkableInterface) inter);
        }
        inter.open(player);
    }

    /**
     * Closes all windows except {@link WalkableInterface} interface types.
     */
    public void close() {
        if (isStandardOpen() || isInputOpen()) {
            player.queue(new CloseWindowsMessageWriter());
            player.resetDialogues();
            setCurrentStandard(null);
            setCurrentInput(null);
        }
    }

    /**
     * Closes the current {@link WalkableInterface}.
     */
    public void closeWalkable() {
        if (isWalkableOpen()) {
            player.queue(new WalkableInterfaceMessageWriter(-1));
            setCurrentWalkable(null);
        }
    }

    /**
     * Closes all interfaces on the game screen.
     */
    public void closeAll() {
        close();
        closeWalkable();
    }

    /**
     * Closes all necessary interfaces on movement or action initialization.
     */
    public void applyActionClose() {

        // Close standard and input interfaces if needed.
        if (isAutoClose(currentStandard) || isAutoClose(currentInput)) {
            close();
        }

        // Close walkable interfaces if needed.
        if (isAutoClose(currentWalkable)) {
            closeWalkable();
        }

        // Reset dialogues.
        player.resetDialogues();
    }

    /**
     * Determines if an interface needs to close on movement or action initialization.
     *
     * @param optional The interface optional.
     * @return {@code true} if the interface needs to close.
     */
    private boolean isAutoClose(Optional<? extends AbstractInterface> optional) {
        return optional.filter(inter -> inter.isAutoClose(player)).isPresent();
    }

    /**
     * Determines if a walkable interface is open.
     *
     * @return {@code true} if a walkable interface is open.
     */
    public boolean isWalkableOpen() {
        return currentWalkable.isPresent();
    }

    /**
     * Determines if an input interface is open.
     *
     * @return {@code true} if an input interface is open.
     */
    public boolean isInputOpen() {
        return currentInput.isPresent();
    }

    /**
     * Determines if a standard interface is open.
     *
     * @return {@code true} if a standard interface is open.
     */
    public boolean isStandardOpen() {
        return currentStandard.isPresent();
    }

    /**
     * Sets the current standard interface.
     *
     * @param inter The new interface.
     */
    private void setCurrentStandard(StandardInterface inter) {
        currentStandard.ifPresent(curr -> curr.onClose(player));
        currentStandard = Optional.ofNullable(inter);
    }

    /**
     * @return The current standard interface.
     */
    public Optional<StandardInterface> getCurrentStandard() {
        return currentStandard;
    }

    /**
     * Resets the current input interface.
     */
    public void resetCurrentInput() {
        setCurrentInput(null);
    }

    /**
     * Sets the current input interface.
     *
     * @param inter The new interface.
     */
    private void setCurrentInput(InputInterface inter) {
        currentInput.ifPresent(curr -> curr.onClose(player));
        currentInput = Optional.ofNullable(inter);
    }

    /**
     * @return The current input interface.
     */
    public Optional<InputInterface> getCurrentInput() {
        return currentInput;
    }

    /**
     * Sets the current walkable interface.
     *
     * @param inter The new interface.
     */
    private void setCurrentWalkable(WalkableInterface inter) {
        currentWalkable.ifPresent(curr -> curr.onClose(player));
        currentWalkable = Optional.ofNullable(inter);
    }

    /**
     * @return The current walkable interface.
     */
    public Optional<WalkableInterface> getCurrentWalkable() {
        return currentWalkable;
    }
}
