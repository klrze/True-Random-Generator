import psycopg2 # Connects to PostgreSQL database
import numpy as np # Numerical Python library
import matplotlib.pyplot as plt # Creates visualizations for data
from scipy.stats import chisquare # To perform mathematical algorithms


def run_chisquare_audit():
    db_config = { # Configuration information (Censored for security purposes)
        "dbname": "-", "user": "-", "password": "-",
        "host": "-", "port": "5432"
    }

    try:
        conn = psycopg2.connect(**db_config) # Establishing connection to database
        cur = conn.cursor() # Creates pipeline to execute queries
        # Fetching only PRNG and TRNG
        cur.execute('SELECT prng_num, trng_num FROM raw_data;') # Passes query to database
        rows = cur.fetchall() # Fetches specified data from all rows

        if not rows: # If no rows found, output message and don't proceed
            print("⚠️ No data found!")
            return

        n = len(rows) # Indicates amount of data that is worked with
        bins = np.linspace(0, 1000, 9) # Equally spaces out data
        # Config for separate charts
        analysis_targets = [
            ("PRNG", [r[0] for r in rows], '#3498db', "ChiSquare_PRNG.pdf"),
            ("TRNG", [r[1] for r in rows], '#2ecc71', "ChiSquare_TRNG.pdf")
        ]

        print(f"--- CHI-SQUARE ANALYSIS (n={n}) ---")

        # Loops using info specified in configuration
        for name, raw_data, color, filename in analysis_targets:
            data = np.array(raw_data) # Inputs data into designated arrays

            # 1. Calculate observed frequencies in each bin
            observed, _ = np.histogram(data, bins=bins)

            # 2. Calculate expected frequencies with uniform distribution to each bin
            expected = np.full(8, n / 8)

            # 3. Perform statistical test
            stat, p_value = chisquare(observed, f_exp=expected)

            plt.figure(figsize=(10, 6)) # Creates window for figure
            plt.bar(range(1, 9), observed, color=color, alpha=0.7, label='Observed Count', edgecolor='black') # Creates bars of histogram
            plt.axhline(y=n / 8, color='red', linestyle='--', linewidth=2, label='Perfectly Uniform (Expected)') # Creates ideal level

            plt.title(f"Chi-Square Test: {name}\nStat: {stat:.4f} | P-Value: {p_value:.4f}", fontsize=14,
                      fontweight='bold')
            plt.xlabel("Value Bin (128 units per bin)", fontsize=12)
            plt.ylabel("Frequency (Count)", fontsize=12)
            plt.legend(loc='upper right')
            plt.grid(axis='y', linestyle=':', alpha=0.6)

            # 5. Save and Show
            plt.tight_layout()
            plt.savefig(filename, dpi=300, bbox_inches='tight')
            print(f"💾 Saved: {filename} | P-Value: {p_value:.4f}")
            plt.show()

        cur.close()
        conn.close()

    except Exception as e:
        print(f"❌ Error: {e}")


if __name__ == "__main__":
    run_chisquare_audit()
