import psycopg2 # Connects to PostgreSQL 
import matplotlib.pyplot as plt # For data visualization
import numpy as np # Numerical Python library

try: # Config information for PostgreSQL
    conn = psycopg2.connect( # Establishing database connection
        dbname="postgres",
        user="postgres",
        password="Klarizzem22",
        host="db.uyrbatqwdfsdvqnkfibf.supabase.co",
        port="5432"
    )
    cur = conn.cursor() # Creates pipeline to execute queries

    # Fetching only PRNG and TRNG
    cur.execute('SELECT prng_num, trng_num FROM raw_data ORDER BY trial_num ASC LIMIT 1000;')
    data = cur.fetchall() # Fetches all required data

    if not data: # If no data found, don't proceed
        print("⚠️ No data found in database!")
    else:
        # Pulls specific data and stores in designated list
        prng_vals = [row[0] for row in data]
        trng_vals = [row[1] for row in data]
        trials = np.arange(len(data)) # Total amount of data

        # Configuration for the 2 separate charts
        chart_configs = [
            (prng_vals, "PRNG", "#3498db", "Flux_Analysis_PRNG.pdf"),
            (trng_vals, "TRNG", "#2ecc71", "Flux_Analysis_TRNG.pdf")
        ]

        # Generate each chart separately
        for dataset, title, color, filename in chart_configs:
            plt.figure(figsize=(12, 5))

            # Plot raw data
            plt.plot(trials, dataset, color=color, alpha=0.3, linewidth=0.8, label="Raw Value")

            # Add Moving Average Trendline
            if len(dataset) > 10:
                window_size = 15
                rolling_avg = np.convolve(dataset, np.ones(window_size) / window_size, mode='valid')
                plt.plot(trials[window_size - 1:], rolling_avg, color=color, linewidth=2.5,
                         label="Stability Trendline")

            plt.title(title, fontsize=16, fontweight='bold', pad=15)
            plt.xlabel("Trial Sequence (Time progression)", fontsize=12)
            plt.ylabel("Generated Value (0-1023)", fontsize=12)
            plt.ylim(-50, 1100)
            plt.grid(True, linestyle=':', alpha=0.6)
            plt.legend(loc='upper right')

            plt.tight_layout()

            # Save the file before showing it
            plt.savefig(filename, dpi=300, bbox_inches='tight')
            print(f"💾 Saved: {filename}")

            plt.show()

    cur.close()
    conn.close()

except Exception as e:
    print(f"❌ Error: {e}")
