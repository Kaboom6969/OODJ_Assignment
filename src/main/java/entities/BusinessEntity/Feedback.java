package entities.BusinessEntity;

import Interfaces.Linkable;
import Interfaces.OwnEntity;
import Tools.EntityHandler;
import Tools.FileHandler.FileDataHandler;
import entities.BaseEntity.AppointmentToFile;
import entities.BaseEntity.FeedbackToFile;
import entities.LazyEntity.LazyEntity;
import entities.Linker.LinkerManager;

import java.util.HashMap;

public class Feedback extends BusinessEntity<FeedbackToFile> implements OwnEntity, Linkable
{
    private LazyEntity<AppointmentToFile> appointment;

    public AppointmentToFile getAppointment()
    {
        return appointment.getSelf();
    }

    public void setAppointment(AppointmentToFile appointmentToFile)
    {
        appointment.changeSelf(appointmentToFile);
    }

    public Feedback(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap)
    {
        this(selfId, selfFile, fileDataHandlerHashMap, linkerManagerHashMap, null);
    }

    public Feedback(String selfId, FileDataHandler selfFile, HashMap<String,FileDataHandler> fileDataHandlerHashMap, HashMap<String, LinkerManager> linkerManagerHashMap, FeedbackToFile self)
    {
        super(selfId, selfFile, self);

        appointment = new LazyEntity<AppointmentToFile>
        (
            linkerManagerHashMap.get(AppointmentToFile.PREFIX).findBasedOnKeyOneResult(selfId, true),
            new EntityHandler(fileDataHandlerHashMap.get(AppointmentToFile.PREFIX))
        );
    }
}
