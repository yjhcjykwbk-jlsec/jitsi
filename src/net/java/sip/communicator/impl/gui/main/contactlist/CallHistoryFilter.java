/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.gui.main.contactlist;

import java.util.*;

import net.java.sip.communicator.impl.gui.*;
import net.java.sip.communicator.impl.gui.main.contactlist.notifsource.*;
import net.java.sip.communicator.service.contactsource.*;
import net.java.sip.communicator.service.gui.*;

/**
 * The <tt>CallHistoryFilter</tt> is a filter over the history contact sources.
 *
 * @author Yana Stamcheva
 * @author Hristo Terezov
 */
public class CallHistoryFilter
    implements  ContactListFilter
{
    /**
     * Applies this filter and stores the result in the given <tt>treeModel</tt>.
     *
     * @param filterQuery the <tt>FilterQuery</tt> that tracks the results of
     * this filtering
     */
    public void applyFilter(FilterQuery filterQuery)
    {
        // First add all notifications.
        NotificationContactSource notificationSource
            = TreeContactList.getNotificationContactSource();

        if (notificationSource != null)
            addMatching(notificationSource);

        Collection<UIContactSource> contactSources
            = GuiActivator.getContactList()
                .getContactSources(ContactSourceService.HISTORY_TYPE);

        TreeContactList contactList = GuiActivator.getContactList();

        // Then add Call history contact source.
        for (UIContactSource contactSource : contactSources)
        {
            // We're in a case of call history contact source.
            ContactQuery query
                = contactSource.getContactSourceService()
                    .createContactQuery("", 50);

            query.addContactQueryListener(contactList);
            filterQuery.addContactQuery(query);
            query.start();

        }
        // Closes this filter to indicate that we finished adding queries to it.
        filterQuery.close();
    }

    /**
     * Indicates if the given <tt>uiContact</tt> is matching this filter.
     *
     * @param uiContact the <tt>UIContact</tt> to check for match
     * @return <tt>true</tt> if the given <tt>uiContact</tt> is matching this
     * filter, <tt>false</tt> otherwise
     */
    public boolean isMatching(UIContact uiContact)
    {
        Object descriptor = uiContact.getDescriptor();

        if (descriptor instanceof SourceContact)
        {
            SourceContact sourceContact = (SourceContact) descriptor;

            if (sourceContact.getContactSource().getType()
                     == ContactSourceService.HISTORY_TYPE)
                return true;
        }
        else if (uiContact instanceof NotificationContact)
            return true;

        return false;
    }

    /**
     * No group could match this filter.
     *
     * @param uiGroup the <tt>UIGroup</tt> to check for match
     * @return <tt>false</tt> to indicate that no group could match this filter
     */
    public boolean isMatching(UIGroup uiGroup)
    {
        return (uiGroup instanceof NotificationGroup);
    }

    /**
     * Adds matching notification contacts to the result tree model.
     *
     * @param notifSource
     */
    private void addMatching(NotificationContactSource notifSource)
    {
        Iterator<? extends UIGroup> notifGroups
            = notifSource.getNotificationGroups();

        while (notifGroups.hasNext())
        {
            UIGroup uiGroup = notifGroups.next();

            Iterator<? extends UIContact> notfications
                = notifSource.getNotifications(uiGroup);

            while (notfications.hasNext())
            {
                GuiActivator.getContactList()
                    .addContact(notfications.next(),
                                uiGroup,
                                false,
                                true);
            }
        }
    }

}
