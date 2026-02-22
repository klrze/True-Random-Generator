import psycopg2 # Connects to PostgreSQL 
import matplotlib.pyplot as plt # For data visualization
import numpy as np # Numerical library

try:
    conn = psycopg2.connect( # Configuration information for PostgreSQL
        dbname="postgres", user="postgres", password="Klarizzem22",
        host="db.uyrbatqwdfsdvqnkfibf.supabase.co", port="5432"
    )
    cur = conn.cursor() # Creates pipeline to create queries
    cur.execute('SELECT prng_num, trng_num FROM raw_data;')  # Only fetching PRNG and TRNG
    rows = cur.fetchall() # Fetches all required data

    if not rows: # If no data found, don't proceed 
        print("⚠️ No data found!")
    else:
        total_trials = len(rows) # Total trial count

        # Config for seperate charts
        # Logic: Divide number by 2, identify remainder
        # Remainder 1: number is odd | Remainder 0: number is even
        # If remainder != 0, add 1 to list
        prng_ones = sum(1 for r in rows if r[0] % 2 != 0)
        trng_ones = sum(1 for r in rows if r[1] % 2 != 0)

        data_to_plot = [ # Config data
            ("PRNG", prng_ones, '#3498db', "Monobit_PRNG.pdf"),
            ("TRNG", trng_ones, '#2ecc71', "Monobit_TRNG.pdf")
        ]

        for label, ones_count, bar_color, filename in data_to_plot:
            # Calculate percentages
            one_pct = (ones_count / total_trials) * 100
            zero_pct = 100 - one_pct

            # 2. Create Figure
            plt.figure(figsize=(7, 8))

            # Plotting the 1s and 0s as a single stacked bar
            plt.bar([0], [one_pct], color=bar_color, alpha=0.8, label='1s (Odd Bits)', width=0.5)
            plt.bar([0], [zero_pct], bottom=[one_pct], color='#e74c3c', alpha=0.8, label='0s (Even Bits)', width=0.5)

            # Add the 50% "Ideal" line
            plt.axhline(y=50, color='black', linestyle='--', linewidth=2, label='Ideal 50% Balance')

            # Labels and Text
            plt.title(f"{label} Monobit Test\n(N = {total_trials})", fontsize=14, fontweight='bold')
            plt.ylabel("Percentage (%)", fontsize=12)
            plt.xticks([0], [label])
            plt.ylim(0, 110)

            # Internal Labels
            plt.text(0, one_pct / 2, f"1s: {one_pct:.2f}%", ha='center', color='white', fontweight='bold', fontsize=12)
            plt.text(0, one_pct + (zero_pct / 2), f"0s: {zero_pct:.2f}%", ha='center', color='white', fontweight='bold',
                     fontsize=12)

            plt.legend(loc='upper right')
            plt.grid(axis='y', linestyle=':', alpha=0.5)

            # Save and Show
            plt.tight_layout()
            plt.savefig(filename, dpi=300, bbox_inches='tight')
            print(f"💾 Saved: {filename}")
            plt.show()

    cur.close()
    conn.close()

except Exception as e:
    print(f"❌ Error: {e}")
