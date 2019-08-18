package com.github.adamantcheese.chan.core.manager;

import androidx.annotation.Nullable;

import com.github.adamantcheese.chan.core.mapper.ThreadMapper;
import com.github.adamantcheese.chan.core.model.ChanThread;
import com.github.adamantcheese.chan.core.model.orm.Loadable;
import com.github.adamantcheese.chan.core.model.save.SerializableThread;
import com.github.adamantcheese.chan.core.repository.SavedThreadLoaderRepository;
import com.github.adamantcheese.chan.core.settings.ChanSettings;
import com.github.adamantcheese.chan.utils.BackgroundUtils;
import com.github.adamantcheese.chan.utils.Logger;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

public class SavedThreadLoaderManager {
    private final static String TAG = "SavedThreadLoaderManager";

    private SavedThreadLoaderRepository savedThreadLoaderRepository;

    @Inject
    public SavedThreadLoaderManager(SavedThreadLoaderRepository savedThreadLoaderRepository) {
        this.savedThreadLoaderRepository = savedThreadLoaderRepository;
    }

    @Nullable
    public ChanThread loadSavedThread(Loadable loadable) {
        if (BackgroundUtils.isMainThread()) {
            throw new RuntimeException("Cannot be executed on the main thread!");
        }

        String threadSubDir = ThreadSaveManager.getThreadSubDir(loadable);
        File threadSaveDir = new File(ChanSettings.saveLocation.get(), threadSubDir);

        if (!threadSaveDir.exists() || !threadSaveDir.isDirectory()) {
            Logger.e(TAG, "threadSaveDir does not exist or is not a directory: "
                    + "(path = " + threadSaveDir.getAbsolutePath()
                    + ", exists = " + threadSaveDir.exists()
                    + ", isDir = " + threadSaveDir.isDirectory() + ")");
            return null;
        }

        File threadFile = new File(threadSaveDir, SavedThreadLoaderRepository.THREAD_FILE_NAME);
        if (!threadFile.exists() || !threadFile.isFile() || !threadFile.canRead()) {
            Logger.e(TAG, "threadFile does not exist or not a file or cannot be read: " +
                    "(path = " + threadFile.getAbsolutePath()
                    + ", exists = " + threadFile.exists()
                    + ", isFile = " + threadFile.isFile()
                    + ", canRead = " + threadFile.canRead() + ")");
            return null;
        }

        File threadSaveDirImages = new File(threadSaveDir, "images");
        if (!threadSaveDirImages.exists() || !threadSaveDirImages.isDirectory()) {
            Logger.e(TAG, "threadSaveDirImages does not exist or is not a directory: "
                    + "(path = " + threadSaveDirImages.getAbsolutePath()
                    + ", exists = " + threadSaveDirImages.exists()
                    + ", isDir = " + threadSaveDirImages.isDirectory() + ")");
            return null;
        }

        try {
            SerializableThread serializableThread = savedThreadLoaderRepository
                    .loadOldThreadFromJsonFile(threadSaveDir);
            if (serializableThread == null) {
                Logger.e(TAG, "Could not load thread from json");
                return null;
            }

            return ThreadMapper.fromSerializedThread(loadable, serializableThread);
        } catch (IOException | SavedThreadLoaderRepository.OldThreadTakesTooMuchSpace e) {
            Logger.e(TAG, "Could not load saved thread", e);
            return null;
        }
    }

}
