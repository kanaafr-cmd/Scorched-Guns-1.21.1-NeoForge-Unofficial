package top.ribs.scguns.compat;

import top.ribs.scguns.ScorchedGuns;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Author: NineZero
 */
@SuppressWarnings("unused")
public class ShoulderSurfingHelper
{
    private static boolean disable1 = false;
    private static boolean disable2 = false;
    private static Method getShoulderInstance;
    private static Method isShoulderSurfing;
    private static Method changePerspective;
    private static Class<?> perspectiveClass;

    public static boolean isShoulderSurfing()
    {
        if(!ScorchedGuns.shoulderSurfingLoaded)
            return false;
        
        if (!disable1)
            try
            {
                init();
                Object object = getShoulderInstance.invoke(null);
                return (boolean) isShoulderSurfing.invoke(object);
            }
            catch(InvocationTargetException | IllegalAccessException | IllegalArgumentException | NullPointerException e)
            {
                ScorchedGuns.LOGGER.error("Shoulder Surfing helper error with method isShoulderSurfing!");
                e.printStackTrace();
                disable1 = true;
            }
        else
        if (!disable2)
            try
            {
                init();
                Object object = getShoulderInstance.invoke(null);
                return (boolean) isShoulderSurfing.invoke(object);
            }
            catch(InvocationTargetException | IllegalAccessException | IllegalArgumentException | NullPointerException e)
            {
                ScorchedGuns.LOGGER.error("Shoulder Surfing helper error with method isShoulderSurfing!");
                e.printStackTrace();
                disable2 = true;
            }
        return false;
    }

    public static void changePerspective(String perspective)
    {
        if(!ScorchedGuns.shoulderSurfingLoaded)
            return;
        
        if (!disable1)
            try
            {
                init();
                Object pov = getPerspective(perspective);
                Object object = getShoulderInstance.invoke(null);
                changePerspective.invoke(object, pov);
            }
            catch(InvocationTargetException | IllegalAccessException | NullPointerException e)
            {
                ScorchedGuns.LOGGER.error("Shoulder Surfing helper error with method changePerspective!");
                e.printStackTrace();
                disable1 = true;
            }
        else
        if (!disable2)
            try
            {
                init();
                Object pov = getPerspective(perspective);
                Object object = getShoulderInstance.invoke(null);
                changePerspective.invoke(object, pov);
            }
            catch(InvocationTargetException | IllegalAccessException | NullPointerException e)
            {
                ScorchedGuns.LOGGER.error("Shoulder Surfing helper error with method changePerspective!");
                e.printStackTrace();
                disable2 = true;
            }
    }

    private static void init()
    {
        if(getShoulderInstance == null)
        {
            try
            {
                Class<?> shoulderSurfingImpl = Class.forName("com.github.exopandora.shouldersurfing.client.ShoulderSurfingImpl");
                perspectiveClass = Class.forName("com.github.exopandora.shouldersurfing.api.model.Perspective");
                getShoulderInstance = shoulderSurfingImpl.getDeclaredMethod("getInstance");
                isShoulderSurfing = shoulderSurfingImpl.getDeclaredMethod("isShoulderSurfing");
                changePerspective = shoulderSurfingImpl.getDeclaredMethod("changePerspective", perspectiveClass);
                
            }
            catch(ClassNotFoundException | NoSuchMethodException | NullPointerException ignored)
            {
            	if (getShoulderInstance!=null)
            	{
                    ScorchedGuns.LOGGER.error("Shoulder Surfing helper failed to load!");
            		ignored.printStackTrace();
            	}
            	disable1 = true;
            }
            if (disable1)
                try
                {
                    Class<?> shoulderInstance = Class.forName("com.github.exopandora.shouldersurfing.client.ShoulderInstance");
                    perspectiveClass = Class.forName("com.github.exopandora.shouldersurfing.api.model.Perspective");
                    getShoulderInstance = shoulderInstance.getDeclaredMethod("getInstance");
                    isShoulderSurfing = shoulderInstance.getDeclaredMethod("doShoulderSurfing");
                    changePerspective = shoulderInstance.getDeclaredMethod("changePerspective", perspectiveClass);
                }
                catch(ClassNotFoundException | NoSuchMethodException | NullPointerException ignored)
                {
                    disable2 = true;
                }
            if (disable1 && disable2)
                ScorchedGuns.LOGGER.info("Shoulder Surfing Reloaded is not installed, proceeding without helper.");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object getPerspective(String perspective) {
        String name = perspective.toUpperCase();
        if (!name.equals("FIRST_PERSON") && !name.equals("THIRD_PERSON_BACK") && !name.equals("THIRD_PERSON_FRONT")) {
            name = "SHOULDER_SURFING";
        }
        return Enum.valueOf((Class<? extends Enum>) perspectiveClass, name);
    }
}
