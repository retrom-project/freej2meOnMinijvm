package com.ebsee.emu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/** Load the shipped MIDI API without silently falling back to the host JDK. */
public final class MidiCompatibilityTest {
    public static void main(String[] args) throws Exception {
        URL[] urls = new URL[args.length];
        for (int i = 0; i < args.length; i++) urls[i] = new File(args[i]).toURI().toURL();
        URLClassLoader loader = new URLClassLoader(urls, null) {
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (!name.startsWith("javax.sound.midi.")) return super.loadClass(name, resolve);
                Class<?> type = findLoadedClass(name);
                if (type == null) type = findClass(name);
                if (resolve) resolveClass(type);
                return type;
            }
        };
        Class<?> sequenceType = loader.loadClass("javax.sound.midi.Sequence");
        Class<?> messageType = loader.loadClass("javax.sound.midi.MidiMessage");
        Class<?> shortType = loader.loadClass("javax.sound.midi.ShortMessage");
        Class<?> eventType = loader.loadClass("javax.sound.midi.MidiEvent");
        Object sequence = sequenceType.getConstructor(float.class, int.class).newInstance(0f, 24);
        Object track = sequenceType.getMethod("createTrack").invoke(sequence);
        for (int i = 0; i < 2; i++) {
            Object message = shortType.getConstructor().newInstance();
            shortType.getMethod("setMessage", int.class, int.class, int.class, int.class)
                .invoke(message, i == 0 ? 0x90 : 0x80, 0, 60, 90);
            Object event = eventType.getConstructor(messageType, long.class).newInstance(message, i * 240L);
            track.getClass().getMethod("add", eventType).invoke(track, event);
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int count = (Integer) loader.loadClass("javax.sound.midi.MidiSystem")
            .getMethod("write", sequenceType, int.class, java.io.OutputStream.class)
            .invoke(null, sequence, 0, output);
        // The independent host parser checks the actual SMF bytes and event timing.
        javax.sound.midi.Sequence parsed = javax.sound.midi.MidiSystem.getSequence(new ByteArrayInputStream(output.toByteArray()));
        if (count != output.size() || parsed.getResolution() != 24 || parsed.getTickLength() != 240 || parsed.getTracks().length != 1)
            throw new AssertionError("MIDI sequence did not round-trip");
        System.out.println("Shipped MIDI API produced independently parsed SMF and event timing.");
        loader.close();
    }
}
