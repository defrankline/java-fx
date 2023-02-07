package ac.udsm.coict.gui;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.sql.SQLException;
import java.util.Objects;

public abstract class DelayedListener<T> implements ChangeListener<T> {
    private final PauseTransition pause = new PauseTransition(javafx.util.Duration.seconds(0.25));
    private T ov, nv;

    public DelayedListener() {
        pause.setOnFinished(event -> {
            if (!Objects.equals(nv, ov)) {
                try {
                    onChanged(nv);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                ov = nv;
            }
        });
    }

    @Override
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        nv = newValue;
        pause.playFromStart();
    }

    public abstract void onChanged(T value) throws SQLException;
}
