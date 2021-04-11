package deu.csc.lecture.homework;

import java.io.Serializable;

public class ToDo implements Serializable {
    private int toDoImage;
    private String task;

    // Constructor that is used to create an instance of the Movie object
    public ToDo(int toDoImage, String task) {
        this.toDoImage = toDoImage;
        this.task = task;
    }

    public int gettoDoImage() {
        return toDoImage;
    }

    public void settoDoImage(int toDoImage) {
        this.toDoImage = toDoImage;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object instanceof ToDo)
        {
            sameSame = this.task.equals(((ToDo) object).getTask());
        }
        return sameSame;
    }
}
