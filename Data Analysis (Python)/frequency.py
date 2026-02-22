import psycopg2 # Connects to PostgreSQL database
import matplotlib.pyplot as plt # For data visualization

try:
    # Configuration information for PostgreSQL (Censored for security purposes)
    conn = psycopg2.connect(
        dbname="-",
        user="-",
        password="-",
        host="-",
        port="-"
    )
    cur = conn.cursor() # Creates pipeline to execute queries

    # Fetching only PRNG and TRNG
    cur.execute('SELECT prng_num, trng_num FROM raw_data LIMIT 1000;')
    data = cur.fetchall() # Fetches specified data from all rows

    if not data: # If no data fetched, don't proceed
        print("⚠️ No data found!")
    else: # Config for seperate charts 
        prng_vals = [row[0] for row in data]
        trng_vals = [row[1] for row in data]
        total = len(data) # Counts amount of rows

        # PRNG Chart
        plt.figure(figsize=(10, 6))
        # Creates bins with even range, counts frequency within individual bins
        plt.hist(prng_vals, bins=10, range=(0, 1000), color='#3498db', edgecolor='black', alpha=0.7) 
        plt.title(f"PRNG - {total} Trials", fontsize=14, fontweight='bold')
        plt.xlabel("Value Range (0-1000)")
        plt.ylabel("Frequency (Count)")
        plt.grid(axis='y', linestyle='--', alpha=0.5)

        # Creates PDF of data
        plt.savefig("PRNG_Distribution.pdf", dpi=300, bbox_inches='tight')
        print("💾 Saved: PRNG_Distribution.pdf")
        plt.show()

        # TRNG chart
        plt.figure(figsize=(10, 6))
        # Creates bins with even range, counts frequency within individual bins
        plt.hist(trng_vals, bins=10, range=(0, 1000), color='#2ecc71', edgecolor='black', alpha=0.7) 
        plt.title(f"TRNG - {total} Trials", fontsize=14, fontweight='bold')
        plt.xlabel("Value Range (0-1000)")
        plt.ylabel("Frequency (Count)")
        plt.grid(axis='y', linestyle='--', alpha=0.5)

        # Creates PDF of data
        plt.savefig("TRNG_Distribution.pdf", dpi=300, bbox_inches='tight')
        print("💾 Saved: TRNG_Distribution.pdf")
        plt.show()

    cur.close()
    conn.close()

except Exception as e:
    print(f"❌ Error: {e}")
