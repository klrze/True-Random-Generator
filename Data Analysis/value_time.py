import psycopg2
import matplotlib.pyplot as plt
import numpy as np

# 1. Database Connection
try:
    conn = psycopg2.connect(
        dbname="postgres",
        user="postgres",
        password="Klarizzem22",
        host="db.uyrbatqwdfsdvqnkfibf.supabase.co",
        port="5432"
    )
    cur = conn.cursor()

    # Get data ordered by ID to ensure the timeline is correct
    cur.execute('SELECT prng_num, qrng_num, trng_num FROM raw_data ORDER BY trial_num ASC LIMIT 1000;')
    data = cur.fetchall()

    if not data:
        print("⚠️ No data found in database!")
    else:
        prng_vals = [row[0] for row in data]
        qrng_vals = [row[1] for row in data]
        trng_vals = [row[2] for row in data]
        trials = np.arange(len(data))

        # 2. Setup the Visualization (3 stacked rows)
        fig, (ax1, ax2, ax3) = plt.subplots(3, 1, figsize=(15, 12), sharex=True)

        plots = [
            (prng_vals, ax1, "Software (PRNG) - Algorithmic", "#3498db"),
            (qrng_vals, ax2, "Quantum (QRNG) - Control Group", "#9b59b6"),
            (trng_vals, ax3, "Hardware (TRNG) - Environmental", "#2ecc71")
        ]

        for raw_data, ax, title, color in plots:
            # Plot the raw "scatter" of points connected by a thin line
            ax.plot(trials, raw_data, color=color, alpha=0.3, linewidth=0.8, label="Raw Value")

            # Add a Moving Average to show "Behavioral Trends"
            # This helps you see if the environment is pushing the TRNG in a certain direction
            if len(raw_data) > 10:
                window = 10
                smooth_data = np.convolve(raw_data, np.ones(window) / window, mode='valid')
                ax.plot(trials[window - 1:], smooth_data, color=color, linewidth=2, label="Trendline")

            ax.set_title(title, loc='left', fontsize=14, fontweight='bold')
            ax.set_ylabel("Value (0-1023)")
            ax.set_ylim(-50, 1100)  # Full 10-bit range
            ax.grid(True, linestyle='--', alpha=0.4)
            ax.legend(loc='upper right')

        plt.xlabel("Trial Number (Time Progression)")
        plt.suptitle(f"Sequential Randomness Flux Analysis ({len(data)} Total Trials)", fontsize=18)

        plt.tight_layout(rect=[0, 0.03, 1, 0.95])
        plt.show()

    cur.close()
    conn.close()

except Exception as e:
    print(f"❌ Error: {e}")
