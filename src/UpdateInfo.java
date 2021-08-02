import java.util.Arrays;
import java.util.List;

public class UpdateInfo
{
    //private final static String updateDescription_4_
    private final static String updateDescription_3_6 = "Исправлена ошибка при создании заказа с несуществующим клиентом";
    private final static String updateDescription_3_7 = "В раздел \"Склад\" добавлено меню \"Конструкции\"";
    private final static String updateDescription_3_8 = "Обновлен раздел \"Светодиоды\":\nВнесены некоторые правки, а так же добавлены функции учёта светодиодов";
    private final static String updateDescription_3_9 = "Внесены правки в \"Создание заказа\":\n " +
            "- Теперь при редактировании заказа есть возможность убрать дизайнера из выпадающего списка (просто выбрать пустое место).\n " +
            "- Текстовое поле с датой заказа теперь нередактируемое, но дату по прежнему можно указать через календарь.";
    private final static String updateDescription_4_0 = "Внесены изменения в раздел \"Светодиоды\":\n " +
            "Теперь таблицы светодиодов резделены по видам. Добавлена кнопка \"Виды светодиодов\" для редактирования, удаления или добавления видов светодиодов. ";
    private final static String updateDescription_4_1 = "Внесены изменения в раздел \"Блоки питания\" :\n " +
            "Теперь таблицы блоков питания резделены по видам корпусов. Также ведётся учёт (приход, расход) и отображается в нижней таблице.\n Хорошего вам дня и отличного настроения!";
    private final static String updateDescription_4_2 = "Теперь через контекстное меню (правой кнопкой мыши) можно создать копию заказа, выбрав \"Повторить\". \n" +
            "В таблицу заказов добавится копия выделенного заказа, но с текущей датой и аккаунтом.";
    private final static String updateDescription_4_4 = "В разделе \"Материалы\" добавлена колонка \"Наличие\", отображающая наличие материала по факту \n " +
            "(даже если материал отсутствует на складе, он может еще некоторое время использоваться в работе, т.е. данный материал еще есть)\n " +
            "Если материал подсвечен красным, значит этот материал полностью отсутствует.\n Спасибо за внимание и хорошего рабочего дня!";
    private final static String updateDescription_4_5 = "Добавлен текущий курс доллара по Центробанку.\nВ разделе \"Светодиоды\" мощность можно теперь вводить и через запятую.\n" +
            "Исправлена ошибка когда при добавлении блока питания с указанием количества колчиество всегда было нулевым.";
    private final static String updateDescription_4_6 = "Небольшие доработки в разделе \"Светотехника\"";
    private final static String updateDescription_4_8 = "Теперь в разделе \"Светотехника\" при операции \"Расход\" можно добавлять примечание.\n" +
            "А также в таблице учета через контекстное меню также можно добавлять примечания";
    private final static String updateDescription_4_14 = "В главном окне в поле \"Примечание\" теперь если " +
            "текст выходит за границы текстового поля, то осуществляетя автоматический перенос на новую строку.";
    private final static String updateDescription_4_15 = "Исправлен баг, когда при закрытии окна создания/редактирования " +
            "заказа, отвечая отрицательно на уточняющий вопрос о закрытии окна, оно все равно закрывалось.";
    private final static String updateDescription_4_16 = "Исправлена ошибка редактирования материала.";
    private final static String updateDescription_4_17 = "В разделе \"Материалы\" добавлена вторая колонка \"Цена продажи\".\nТакже теперь при создании заказа менеджерами в выпадающем списке менеджеров теперь автоматически указывается залогинившийся менеджер.";
    private final static String updateDescription_4_18 = "В меню \"Склад\" добавлено новое подменю \"Полиграфия\"";
    private final static String updateDescription_4_19 = "Исправлены некоторые \"баги\", вследствие которых добавленные БП, Светодиоды или Конструкции не отображались в таблицах.";
    private final static String updateDescription_4_20 = "В разделе \"Светотехника\" добавленные в учёте примечания теперь можно редактировать";
    private final static String updateDescription_4_21 = "В разделе \"Светодиоды\" исправлены глюки с добавлением новых видов светодиодов, которые не отображались в выпадающем меню после добавления.";
    private final static String updateDescription_4_22 = "Добавлен раздел \"Заявки\" в меню \"Склад\".\nТакже заявки можно добавлять из соответствующих разделов (например, \"Светодиоды\", \"Конструкции\" и др.) через контекстное меню (правая кнопка мыши по таблице).";
    private final static String updateDescription_4_23 = "Добавлена функция поиска заказа по номеру квитанции.\n\nТакже при появлении новых заявок будет отображаться уведомление и кнопка перехода в раздел \"Заявки\" (Только для Елены Николаевны)";
    private final static String updateDescription_4_24 = "Исправлены ошибки в методах addValue() и editValue() класса MaterialsForm, из-за которых при инкапсуляции статичных переменных, под которые в стеке уже выделилась память, но при этом поле active классов MaterialsValue и MaterialsKind оставалось false, поэтому при выполнении геттера DatabaseStorehouse в массив activeList переменные не добавлялись.";

    private final static List<Integer> _updatesList = Arrays.asList(36, 37, 38, 39, 40, 41, 42, 44, 45, 46, 48, 414, 415, 416, 417, 418, 419, 420, 421, 422, 423, 424);

    static String getUpdateDescription(final int vers)
    {
        String updateDescription;
        switch (vers)
        {
            case 36:
                updateDescription = updateDescription_3_6;
                break;
            case 37:
                updateDescription = updateDescription_3_7;
                break;
            case 38:
                updateDescription = updateDescription_3_8;
                break;
            case 39:
                updateDescription = updateDescription_3_9;
                break;
            case 40:
                updateDescription = updateDescription_4_0;
                break;
            case 41:
                updateDescription = updateDescription_4_1;
                break;
            case 42:
                updateDescription = updateDescription_4_2;
                break;
            case 44:
                updateDescription = updateDescription_4_4;
                break;
            case 45:
                updateDescription = updateDescription_4_5;
                break;
            case 46:
                updateDescription = updateDescription_4_6;
                break;
            case 48:
                updateDescription = updateDescription_4_8;
                break;
            case 414:
                updateDescription = updateDescription_4_14;
                break;
            case 415:
                updateDescription = updateDescription_4_15;
                break;
            case 416:
                updateDescription = updateDescription_4_16;
                break;
            case 417:
                updateDescription = updateDescription_4_17;
                break;
            case 418:
                updateDescription = updateDescription_4_18;
                break;
            case 419:
                updateDescription = updateDescription_4_19;
                break;
            case 420:
                updateDescription = updateDescription_4_20;
                break;
            case 421:
                updateDescription = updateDescription_4_21;
                break;
            case 422:
                updateDescription = updateDescription_4_22;
                break;
            case 423:
                updateDescription = updateDescription_4_23;
                break;
            case 424:
                updateDescription = updateDescription_4_24;
                break;
            default:
                updateDescription = "";
        }
        return updateDescription;
    }

    static String getVersion(int vers, boolean primary)
    {
        String version;
        switch (vers)
        {
            case 36:
                if (primary)
                    version = "3";
                else
                    version = "6";
                break;
            case 37:
                if (primary)
                    version = "3";
                else
                    version = "7";
                break;
            case 38:
                if (primary)
                    version = "3";
                else
                    version = "8";
                break;
            case 39:
                if (primary)
                    version = "3";
                else
                    version = "9";
                break;
            case 40:
                if (primary)
                    version = "4";
                else
                    version = "0";
                break;
            case 41:
                if (primary)
                    version = "4";
                else
                    version = "1";
                break;
            case 42:
                if (primary)
                    version = "4";
                else
                    version = "2";
                break;
            case 44:
            if (primary)
                version = "4";
            else
                version = "4";
            break;
            case 45:
                if (primary)
                    version = "4";
                else
                    version = "5";
                break;
            case 46:
                if (primary)
                    version = "4";
                else
                    version = "6";
                break;
            case 48:
                if (primary)
                    version = "4";
                else
                    version = "8";
                break;
            case 414:
                if (primary)
                    version = "4";
                else
                    version = "14";
                break;
            case 415:
                if (primary)
                    version = "4";
                else
                    version = "15";
                break;
            case 416:
                if (primary)
                    version = "4";
                else
                    version = "16";
                break;
            case 417:
                if (primary)
                    version = "4";
                else
                    version = "17";
                break;
            case 418:
                if (primary)
                    version = "4";
                else
                    version = "18";
                break;
            case 419:
                if (primary)
                    version = "4";
                else
                    version = "19";
                break;
            case 420:
                if (primary)
                    version = "4";
                else
                    version = "20";
                break;
            case 421:
                if (primary)
                    version = "4";
                else
                    version = "21";
                break;
            case 422:
                if (primary)
                    version = "4";
                else
                    version = "22";
                break;
            case 423:
                if (primary)
                    version = "4";
                else
                    version = "23";
                break;
            case 424:
                if (primary)
                    version = "4";
                else
                    version = "24";
                break;

            default:
                version = "";
        }
        return version;
    }

    public static List<Integer> getUpdatesList()
    {
        return _updatesList;
    }
}
